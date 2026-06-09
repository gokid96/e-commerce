package com.github.gokid96.e_commerce.order.application;

import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResult {

    @Getter
    public static class Order{
        private final Long orderId;
        private final long totalPrice;
        private final long discountPrice;

        @Builder
        private Order(Long orderId, long totalPrice, long discountPrice) {
            this.orderId = orderId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
        }
        public static Order of(OrderInfo.Order info) {
            return Order.builder()
                    .orderId(info.getOrderId())
                    .totalPrice(info.getTotalPrice())
                    .discountPrice(info.getDiscountPrice())
                    .build();
        }
    }
}
