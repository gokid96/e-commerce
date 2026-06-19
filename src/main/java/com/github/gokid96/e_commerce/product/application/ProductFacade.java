package com.github.gokid96.e_commerce.product.application;

import com.github.gokid96.e_commerce.product.domain.product.ProductCommand;
import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import com.github.gokid96.e_commerce.product.domain.stock.StockInfo;
import com.github.gokid96.e_commerce.product.domain.stock.StockService;
import com.github.gokid96.e_commerce.rank.domain.RankCommand;
import com.github.gokid96.e_commerce.rank.domain.RankInfo;
import com.github.gokid96.e_commerce.rank.domain.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private static final int RECENT_DAYS = 3;
    private static final int TOP_LIMIT = 5;

    private final ProductService productService;
    private final StockService stockService;
    private final RankService rankService;

    @Transactional(readOnly = true)
    public ProductResult.Products getProducts() {
        ProductInfo.Products products = productService.getSellingProducts();
        return toResult(products);
    }

    @Transactional(readOnly = true)
    public ProductResult.Products getPopularProducts() {
        // PaymentInfo.Orders completedOrders = paymentService.getCompletedOrdersBetweenDays(RECENT_DAYS);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(RECENT_DAYS);

        // OrderCommand.TopOrders command = OrderCommand.TopOrders.of(completedOrders.getOrderIds(), TOP_LIMIT);
        // OrderInfo.TopPaidProducts topPaidProducts = orderService.getTopPaidProducts(command);
        RankCommand.PopularSellRank command = RankCommand.PopularSellRank.of(TOP_LIMIT, startDate, endDate);
        RankInfo.PopularProducts popularProducts = rankService.getPopularSellRank(command);

        ProductInfo.Products products = productService.getProducts(
                //        ProductCommand.Products.of(topPaidProducts.getProductIds()));
                ProductCommand.Products.of(popularProducts.getProductIds()));
        return toResult(products);
    }

    private ProductResult.Products toResult(ProductInfo.Products products) {
        return ProductResult.Products.of(products.getProducts().stream()
                .map(this::toProductResult)
                .toList());
    }

    private ProductResult.Product toProductResult(ProductInfo.Product product) {
        StockInfo.Stock stock = stockService.getStock(product.getProductId());
        return ProductResult.Product.of(
                product.getProductId(), product.getProductName(), product.getProductPrice(), stock.getQuantity());
    }
}