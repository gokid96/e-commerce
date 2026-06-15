package com.github.gokid96.e_commerce.product.application;

import com.github.gokid96.e_commerce.order.domain.OrderCommand;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.payment.domain.PaymentInfo;
import com.github.gokid96.e_commerce.payment.domain.PaymentService;
import com.github.gokid96.e_commerce.product.domain.product.ProductCommand;
import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import com.github.gokid96.e_commerce.product.domain.stock.StockInfo;
import com.github.gokid96.e_commerce.product.domain.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private static final int RECENT_DAYS = 3;
    private static final int TOP_LIMIT = 5;

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final ProductService productService;
    private final StockService stockService;

    @Transactional(readOnly = true)
    public ProductResult.Products getProducts() {
        ProductInfo.Products products = productService.getSellingProducts();
        return toResult(products);
    }

    @Transactional(readOnly = true)
    public ProductResult.Products getPopularProducts() {
        PaymentInfo.Orders completedOrders = paymentService.getCompletedOrdersBetweenDays(RECENT_DAYS);

        OrderCommand.TopOrders command = OrderCommand.TopOrders.of(completedOrders.getOrderIds(), TOP_LIMIT);
        OrderInfo.TopPaidProducts topPaidProducts = orderService.getTopPaidProducts(command);

        ProductInfo.Products products = productService.getProducts(
                ProductCommand.Products.of(topPaidProducts.getProductIds()));
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