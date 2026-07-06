package com.github.gokid96.e_commerce.rank.application;

import com.github.gokid96.e_commerce.common.cache.CacheType;
import com.github.gokid96.e_commerce.common.cache.infrastructure.RedisCacheTemplate;
import com.github.gokid96.e_commerce.product.domain.product.Product;
import com.github.gokid96.e_commerce.product.domain.product.ProductRepository;
import com.github.gokid96.e_commerce.product.domain.product.ProductSellingStatus;
import com.github.gokid96.e_commerce.rank.domain.Rank;
import com.github.gokid96.e_commerce.rank.domain.RankRepository;
import com.github.gokid96.e_commerce.support.IntegrationTestSupport;
import com.github.gokid96.e_commerce.support.database.DatabaseCleaner;
import com.github.gokid96.e_commerce.support.database.RedisCacheCleaner;
import com.github.gokid96.e_commerce.support.database.RedisKeyCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RankFacadeCacheTest extends IntegrationTestSupport {

    private static final String CACHE_KEY = "top:5:days:3"; // @Cacheable key (top/days 조립) 와 동일

    @Autowired private RankFacade rankFacade;
    @Autowired private ProductRepository productRepository;
    @Autowired private RankRepository rankRepository;
    @Autowired private RedisCacheTemplate redisCacheTemplate;
    @Autowired private RedisCacheCleaner redisCacheCleaner;
    @Autowired private DatabaseCleaner databaseCleaner;
    @Autowired private RedisKeyCleaner redisKeyCleaner;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();
        redisCacheCleaner.clean();
        redisKeyCleaner.clean();

        product1 = productRepository.save(Product.create("상품1", 1_000L, ProductSellingStatus.SELLING));
        product2 = productRepository.save(Product.create("상품2", 2_000L, ProductSellingStatus.SELLING));
        product3 = productRepository.save(Product.create("상품3", 3_000L, ProductSellingStatus.SELLING));

        rankRepository.save(Rank.createSell(product1.getId(), LocalDate.now().minusDays(1), 10));
        rankRepository.save(Rank.createSell(product2.getId(), LocalDate.now().minusDays(1), 34));
        rankRepository.save(Rank.createSell(product3.getId(), LocalDate.now().minusDays(2), 42));
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.clean();
        redisCacheCleaner.clean();
        redisKeyCleaner.clean();
    }

    @DisplayName("인기 상품을 조회하면 결과가 캐시에 저장된다.")
    @Test
    void getPopularProducts() {
        Optional<RankResult.PopularProducts> empty =
                redisCacheTemplate.get(CacheType.POPULAR_PRODUCT, CACHE_KEY, RankResult.PopularProducts.class);

        rankFacade.getPopularProducts(RankCriteria.PopularProducts.ofTop5Days3());

        assertThat(empty).isEmpty();
        RankResult.PopularProducts cached = redisCacheTemplate
                .get(CacheType.POPULAR_PRODUCT, CACHE_KEY, RankResult.PopularProducts.class).orElseThrow();
        assertThat(cached.getProducts())
                .extracting("productId")
                .containsExactly(product3.getId(), product2.getId(), product1.getId());
    }

    @DisplayName("인기 상품을 @CachePut으로 캐시에 갱신한다.")
    @Test
    void updatePopularProductsForCache() {
        Optional<RankResult.PopularProducts> empty =
                redisCacheTemplate.get(CacheType.POPULAR_PRODUCT, CACHE_KEY, RankResult.PopularProducts.class);

        rankFacade.updatePopularProducts(RankCriteria.PopularProducts.ofTop5Days3());

        assertThat(empty).isEmpty();
        RankResult.PopularProducts cached = redisCacheTemplate
                .get(CacheType.POPULAR_PRODUCT, CACHE_KEY, RankResult.PopularProducts.class).orElseThrow();
        assertThat(cached.getProducts())
                .extracting("productId")
                .containsExactly(product3.getId(), product2.getId(), product1.getId());
    }

    @DisplayName("기존 캐시가 있어도 갱신하면 최신 내용으로 덮어쓴다.")
    @Test
    void updatePopularProductsForRefresh() {
        redisCacheTemplate.put(CacheType.POPULAR_PRODUCT, CACHE_KEY, "stale");
        Optional<String> stale = redisCacheTemplate.get(CacheType.POPULAR_PRODUCT, CACHE_KEY, String.class);

        rankFacade.updatePopularProducts(RankCriteria.PopularProducts.ofTop5Days3());

        assertThat(stale).contains("stale");
        RankResult.PopularProducts cached = redisCacheTemplate
                .get(CacheType.POPULAR_PRODUCT, CACHE_KEY, RankResult.PopularProducts.class).orElseThrow();
        assertThat(cached.getProducts())
                .extracting("productId")
                .containsExactly(product3.getId(), product2.getId(), product1.getId());
    }
}
