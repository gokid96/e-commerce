package com.github.gokid96.e_commerce.rank.application;

import com.github.gokid96.e_commerce.common.cache.CacheType;
import com.github.gokid96.e_commerce.product.domain.product.ProductCommand;
import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import com.github.gokid96.e_commerce.rank.domain.RankCommand;
import com.github.gokid96.e_commerce.rank.domain.RankInfo;
import com.github.gokid96.e_commerce.rank.domain.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RankFacade {

    private final RankService rankService;
    private final ProductService productService;

    @Cacheable(cacheNames = CacheType.CacheName.POPULAR_PRODUCT,
            key = "'top:' + #criteria.top + ':days:' + #criteria.days")
    @Transactional(readOnly = true)
    public RankResult.PopularProducts getPopularProducts(RankCriteria.PopularProducts criteria) {
        return findPopularProducts(criteria.getTop(), criteria.getDays());
    }

    @CachePut(cacheNames = CacheType.CacheName.POPULAR_PRODUCT,
            key = "'top:' + #criteria.top + ':days:' + #criteria.days")
    @Transactional(readOnly = true)
    public RankResult.PopularProducts updatePopularProducts(RankCriteria.PopularProducts criteria) {
        return findPopularProducts(criteria.getTop(), criteria.getDays());
    }

    private RankResult.PopularProducts findPopularProducts(int top, int days) {
        RankCommand.PopularSellRank command = RankCommand.PopularSellRank.of(top, days, LocalDate.now());
        RankInfo.PopularProducts popularProducts = rankService.getPopularSellRank(command);

        ProductInfo.Products products = productService.getProducts(
                ProductCommand.Products.of(popularProducts.getProductIds()));

        return RankResult.PopularProducts.of(products.getProducts().stream()
                .map(this::toPopularProduct)
                .toList());
    }

    private RankResult.PopularProduct toPopularProduct(ProductInfo.Product product) {
        return RankResult.PopularProduct.of(
                product.getProductId(), product.getProductName(), product.getProductPrice());
    }

    @Transactional
    public void persistDailyRank(RankCriteria.PersistDailyRank criteria) {
        rankService.persistDailyRank(criteria.getDate());
    }

}
