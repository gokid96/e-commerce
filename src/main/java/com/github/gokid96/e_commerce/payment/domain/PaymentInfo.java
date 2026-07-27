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
}