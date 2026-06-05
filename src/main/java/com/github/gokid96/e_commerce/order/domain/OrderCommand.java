package com.github.gokid96.e_commerce.order.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderCommand {

    @Getter
    public static class Create {
        private final Long userId;
        private final Long userCouponId;
        private final double discountRate;
        private final List<OrderProduct> products;


        @Builder
        private Create(Long userId, Long userCouponId, double discountRate, List<OrderProduct> products) {
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.discountRate = discountRate;
            this.products = products;
        }

        public static Create of(Long userId, Long userCouponId, double discountRate, List<OrderProduct> products) {
            return Create.builder()
                    .userId(userId)
                    .userCouponId(userCouponId)
                    .discountRate(discountRate)
                    .products(products)
                    .build();
        }
    }

    @Getter
    public static class OrderProduct {
        private final Long productId;
        private final String productName;
        private final long quantityPrice;
        private final int quantity;

        @Builder
        private OrderProduct(Long productId, String productName, long quantityPrice, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.quantityPrice = quantityPrice;
            this.quantity = quantity;

        }

        public static OrderProduct of(Long productId, String productName, long quantityPrice, int quantity) {
            return OrderProduct.builder()
                    .productId(productId)
                    .productName(productName)
                    .quantityPrice(quantityPrice)
                    .quantity(quantity)
                    .build();
        }

    }
}
