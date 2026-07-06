package com.github.gokid96.e_commerce.rank.application;

import com.github.gokid96.e_commerce.order.domain.Order;
import com.github.gokid96.e_commerce.order.domain.OrderProduct;
import com.github.gokid96.e_commerce.order.domain.OrderRepository;
import com.github.gokid96.e_commerce.product.domain.product.Product;
import com.github.gokid96.e_commerce.product.domain.product.ProductRepository;
import com.github.gokid96.e_commerce.product.domain.product.ProductSellingStatus;
import com.github.gokid96.e_commerce.rank.domain.Rank;
import com.github.gokid96.e_commerce.rank.domain.RankCommand;
import com.github.gokid96.e_commerce.rank.domain.RankInfo;
import com.github.gokid96.e_commerce.rank.domain.RankKey;
import com.github.gokid96.e_commerce.rank.domain.RankKeys;
import com.github.gokid96.e_commerce.rank.domain.RankRepository;
import com.github.gokid96.e_commerce.rank.domain.RankType;
import com.github.gokid96.e_commerce.support.IntegrationTestSupport;
import com.github.gokid96.e_commerce.support.database.RedisCacheCleaner;
import com.github.gokid96.e_commerce.support.database.RedisKeyCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class RankFacadeIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private RankFacade rankFacade;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RankRepository rankRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisCacheCleaner redisCacheCleaner;

    @Autowired
    private RedisKeyCleaner redisKeyCleaner;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    @BeforeEach
    void setUp() {
        redisCacheCleaner.clean();
        redisKeyCleaner.clean();
    }

    @AfterEach
    void tearDown() {
        redisCacheCleaner.clean();
        redisKeyCleaner.clean();
    }

    @DisplayName("일별 랭킹을 생성한다.")
    @Test
    void createDailyRankAt() {
        // given
        Order order1 = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(1L, "상품1", 10_000L, 2),
                OrderProduct.create(2L, "상품2", 20_000L, 3)));
        Order order2 = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(1L, "상품1", 10_000L, 2),
                OrderProduct.create(3L, "상품3", 30_000L, 4)));
        Order order3 = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(2L, "상품2", 20_000L, 3),
                OrderProduct.create(3L, "상품3", 30_000L, 4)));

        List.of(order1, order2, order3).forEach(o -> {
            o.paid(LocalDateTime.of(2026, 6, 22, 12, 0, 0));
            orderRepository.save(o);
        });

        // when
        rankFacade.createDailyRankAt(LocalDate.of(2026, 6, 23));

        // then: 랭크가 Redis 일별 키에 쌓이므로 union 조회로 검증
        RankCommand.Query command = RankCommand.Query.of(3,
                RankKey.ofDays(RankType.SELL, 3),
                RankKeys.ofDaysWithDate(RankType.SELL, 1, LocalDate.of(2026, 6, 23)));
        List<RankInfo.PopularProduct> result = rankRepository.findPopularSellRanks(command);
        assertThat(result).hasSize(3)
                .extracting(RankInfo.PopularProduct::getProductId)
                .containsExactly(3L, 2L, 1L); // 상품3(8) > 상품2(6) > 상품1(4)
    }

    @DisplayName("최근 3일 판매 랭킹 상위 상품을 조회한다.")
    @Test
    void getPopularProducts() {
        // given
        Product product1 = productRepository.save(Product.create("상품1", 1_000L, ProductSellingStatus.SELLING));
        Product product2 = productRepository.save(Product.create("상품2", 2_000L, ProductSellingStatus.SELLING));
        Product product3 = productRepository.save(Product.create("상품3", 3_000L, ProductSellingStatus.STOP_SELLING));

        rankRepository.save(Rank.createSell(product1.getId(), LocalDate.now().minusDays(1), 10));
        rankRepository.save(Rank.createSell(product2.getId(), LocalDate.now().minusDays(1), 34));
        rankRepository.save(Rank.createSell(product3.getId(), LocalDate.now().minusDays(2), 42));

        // when
        RankResult.PopularProducts result =
                rankFacade.getPopularProducts(RankCriteria.PopularProducts.ofTop5Days3());

        // then
        assertThat(result.getProducts()).hasSize(3)
                .extracting("productId")
                .containsExactly(product3.getId(), product2.getId(), product1.getId());
    }

    @DisplayName("여러 날의 점수를 합산해 인기 상품을 조회하고 캐시를 갱신한다.")
    @Test
    void updatePopularProducts() {
        // given
        Product p1 = productRepository.save(Product.create("상품1", 1_000L, ProductSellingStatus.SELLING));
        Product p2 = productRepository.save(Product.create("상품2", 2_000L, ProductSellingStatus.SELLING));
        Product p3 = productRepository.save(Product.create("상품3", 3_000L, ProductSellingStatus.SELLING));

        LocalDate today = LocalDate.now();
        rankRepository.save(Rank.createSell(p1.getId(), today, 1));
        rankRepository.save(Rank.createSell(p1.getId(), today.minusDays(1), 2));
        rankRepository.save(Rank.createSell(p2.getId(), today, 9));
        rankRepository.save(Rank.createSell(p3.getId(), today.minusDays(2), 5));

        // when
        RankResult.PopularProducts result =
                rankFacade.updatePopularProducts(RankCriteria.PopularProducts.ofTop5Days3());

        // then: p2(9) > p3(5) > p1(1+2=3), 합산 키 ZSET에는 상품 3개
        Long size = redisTemplate.opsForZSet().size(RankKey.ofDays(RankType.SELL, 3).generate());
        assertThat(size).isEqualTo(3);
        assertThat(result.getProducts()).hasSize(3)
                .extracting("productId")
                .containsExactly(p2.getId(), p3.getId(), p1.getId());
    }
}