package com.github.gokid96.e_commerce.order.interfaces.dto;

import com.github.gokid96.e_commerce.order.application.OrderResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponse {

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

        public static Order of(OrderResult.Order result) {
            return Order.builder()
                    .orderId(result.getOrderId())
                    .totalPrice(result.getTotalPrice())
                    .discountPrice(result.getDiscountPrice())
                    .build();
        }
    }
}