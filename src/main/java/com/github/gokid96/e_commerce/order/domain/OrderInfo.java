package com.github.gokid96.e_commerce.order.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderInfo {

    @Getter
    public static class Order {
        private final Long orderId;
        private final long totalPrice;
        private final long discountPrice;

        @Builder
        private Order(Long orderId, long totalPrice, long discountPrice) {
            this.orderId = orderId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
        }

        public static Order of(com.github.gokid96.e_commerce.order.domain.Order order) {
            return Order.builder()
                    .orderId(order.getId())
                    .totalPrice(order.getTotalPrice())
                    .discountPrice(order.getDiscountPrice())
                    .build();
        }
    }

    @Getter
    public static class TopPaidProducts {
        private final List<Long> productIds;

        @Builder
        private TopPaidProducts(List<Long> productIds) {
            this.productIds = productIds;
        }

        public static TopPaidProducts of(List<Long> productIds) {
            return TopPaidProducts.builder()
                    .productIds(productIds)
                    .build();
        }
    }
}
