package com.github.gokid96.e_commerce.message.domain;

import com.github.gokid96.e_commerce.order.domain.Order;
import com.github.gokid96.e_commerce.order.domain.OrderEvent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageCommand {

    @Getter
    public static class Order {
        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final LocalDateTime paidAt;

        @Builder
        private Order(Long orderId, Long userId, Long userCouponId,
                      long totalPrice, long discountPrice, LocalDateTime paidAt) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
            this.paidAt = paidAt;
        }

        public static Order of(OrderEvent.Paid event) {
            return Order.builder()
                    .orderId(event.getOrderId())
                    .userId(event.getUserId())
                    .userCouponId(event.getUserCouponId())
                    .totalPrice(event.getTotalPrice())
                    .discountPrice(event.getDiscountPrice())
                    .paidAt(event.getPaidAt())
                    .build();
        }
    }
}