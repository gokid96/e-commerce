package com.github.gokid96.e_commerce.payment.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentInfo {

    @Getter
    public static class Payment {

        private final Long paymentId;

        private Payment(Long paymentId) {
            this.paymentId = paymentId;
        }

        public static Payment of(Long paymentId) {
            return new Payment(paymentId);
        }
    }
    @Getter
    public static class Order {
        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;

        private Order(Long orderId, Long userId, Long userCouponId, long totalPrice) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.totalPrice = totalPrice;
        }

        public static Order of(Long orderId, Long userId, Long userCouponId, long totalPrice) {
            return new Order(orderId, userId, userCouponId, totalPrice);
        }
    }
}