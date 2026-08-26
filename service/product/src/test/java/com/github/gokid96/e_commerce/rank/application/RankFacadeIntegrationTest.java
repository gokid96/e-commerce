package com.github.gokid96.e_commerce.rank.application;

import com.github.gokid96.e_commerce.product.domain.product.Product;
import com.github.gokid96.e_commerce.product.domain.product.ProductRepository;
import com.github.gokid96.e_commerce.product.domain.product.ProductSellingStatus;
import com.github.gokid96.e_commerce.rank.domain.Rank;
import com.github.gokid96.e_commerce.rank.domain.RankKey;
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

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class RankFacadeIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private RankFacade rankFacade;

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