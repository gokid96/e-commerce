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
        private final long amount;

        @Builder
        private Payment(Long orderId, long amount) {
            this.orderId = orderId;
            this.amount = amount;
        }
    public static Payment of (Long orderId, long amount) {
            return Payment.builder()
                    .orderId(orderId)
                    .amount(amount)
                    .build();
    }

    }
}
