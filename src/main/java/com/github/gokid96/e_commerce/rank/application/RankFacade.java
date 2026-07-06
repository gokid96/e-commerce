package com.github.gokid96.e_commerce.rank.application;

import com.github.gokid96.e_commerce.common.cache.CacheType;
import com.github.gokid96.e_commerce.order.domain.OrderCommand;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.domain.OrderService;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankFacade {

    private final OrderService orderService;
    private final RankService rankService;
    private final ProductService productService;

    @Transactional
    public void createDailyRankAt(LocalDate date) {
        OrderCommand.DateQuery orderCommand = OrderCommand.DateQuery.of(date);
        OrderInfo.PaidProducts paidProducts = orderService.getPaidProducts(orderCommand);

        RankCommand.CreateList rankCommand = createListCommand(paidProducts, date);
        rankService.createSellRank(rankCommand);
    }

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

    private RankCommand.CreateList createListCommand(OrderInfo.PaidProducts paidProducts, LocalDate date) {
        List<RankCommand.Create> commands = paidProducts.getProducts().stream()
                .map(product -> createCommand(product, date))
                .toList();
        return RankCommand.CreateList.of(commands);
    }

    private RankCommand.Create createCommand(OrderInfo.PaidProduct product, LocalDate date) {
        return RankCommand.Create.of(product.getProductId(), product.getQuantity(), date);
    }

    @Transactional
    public void persistDailyRank(RankCriteria.PersistDailyRank criteria) {
        rankService.persistDailyRank(criteria.getDate());
    }

}
