package com.github.gokid96.e_commerce.order.infrastructure.client;

import com.github.gokid96.e_commerce.order.domain.OrderClient;
import com.github.gokid96.e_commerce.order.domain.OrderCommand;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderApiClient implements OrderClient {

    @Override
    public OrderInfo.User getUser(Long userId) {
        return null;
    }

    @Override
    public List<OrderInfo.Product> getProducts(List<OrderCommand.OrderProduct> command) {
        return List.of(
                OrderInfo.Product.of(1L, "Sample Product", 10_000L, 100),
                OrderInfo.Product.of(2L, "Another Product", 20_000L, 50)
        );
    }

    @Override
    public OrderInfo.Coupon getUsableCoupon(Long userCouponId) {
        return null;
    }

    @Override
    public void deductStock(List<OrderCommand.OrderProduct> products) {

    }

    @Override
    public void restoreStock(List<OrderCommand.OrderProduct> products) {

    }
}
