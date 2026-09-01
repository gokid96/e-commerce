package com.github.gokid96.e_commerce.order.infrastructure;

import com.github.gokid96.e_commerce.common.client.api.coupon.CouponApiClient;
import com.github.gokid96.e_commerce.common.client.api.coupon.CouponResponse;
import com.github.gokid96.e_commerce.common.client.api.product.ProductApiClient;
import com.github.gokid96.e_commerce.common.client.api.product.ProductRequest;
import com.github.gokid96.e_commerce.common.client.api.product.ProductResponse;
import com.github.gokid96.e_commerce.common.client.api.product.StockRequest;
import com.github.gokid96.e_commerce.order.domain.OrderClient;
import com.github.gokid96.e_commerce.order.domain.OrderCommand;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderCoreClient implements OrderClient {

    private final ProductApiClient productApiClient;
    private final CouponApiClient couponApiClient;

    @Override
    public List<OrderInfo.Product> getProducts(List<OrderCommand.OrderProduct> command) {
        Map<Long, Integer> orderQuantities = command.stream()
                .collect(Collectors.toMap(
                        OrderCommand.OrderProduct::getProductId,
                        OrderCommand.OrderProduct::getQuantity,
                        Integer::sum
                ));

        ProductResponse.Products products = productApiClient.getProducts(
                ProductRequest.Products.of(
                        command.stream()
                                .map(OrderCommand.OrderProduct::getProductId)
                                .toList()
                )
        ).getData();

        return products.getProducts().stream()
                .map(product -> OrderInfo.Product.of(
                        product.getProductId(),
                        product.getProductName(),
                        product.getProductPrice(),
                        orderQuantities.get(product.getProductId())
                ))
                .toList();
    }

    @Override
    public OrderInfo.Coupon getUsableCoupon(Long userCouponId) {
        CouponResponse.UserCoupon coupon = couponApiClient.getUsableCoupon(userCouponId).getData();
        return OrderInfo.Coupon.of(
                coupon.getUserCouponId(),
                coupon.getCouponId(),
                coupon.getCouponName(),
                coupon.getDiscountRate(),
                coupon.getIssuedAt()
        );
    }

    @Override
    public void deductStock(List<OrderCommand.OrderProduct> products) {
        productApiClient.deductStock(
                StockRequest.Deduct.of(
                        products.stream()
                                .map(p -> StockRequest.Product.of(p.getProductId(), p.getQuantity()))
                                .toList()
                )
        );
    }

    @Override
    public void restoreStock(List<OrderCommand.OrderProduct> products) {
        productApiClient.restoreStock(
                StockRequest.Restore.of(
                        products.stream()
                                .map(p -> StockRequest.Product.of(p.getProductId(), p.getQuantity()))
                                .toList()
                )
        );
    }
}
