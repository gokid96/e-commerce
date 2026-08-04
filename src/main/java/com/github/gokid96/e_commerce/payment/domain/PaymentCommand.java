package com.github.gokid96.e_commerce.payment.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentCommand {

    @Getter
    public static class Payment {
        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long amount;

        @Builder
        private Payment(Long orderId, Long userId, Long userCouponId, long amount) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.amount = amount;
        }

        public static Payment of(Long orderId, Long userId, Long userCouponId, long amount) {
            return Payment.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .userCouponId(userCouponId)
                    .amount(amount)
                    .build();
        }
    }
}
