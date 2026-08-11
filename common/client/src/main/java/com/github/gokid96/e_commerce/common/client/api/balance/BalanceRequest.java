package com.github.gokid96.e_commerce.common.client.api.balance;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Use {
        private Long amount;

        @Builder
        private Use(Long amount) {
            this.amount = amount;
        }

        public static Use of(Long amount) {
            return Use.builder().amount(amount).build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Refund {
        private Long amount;

        @Builder
        private Refund(Long amount) {
            this.amount = amount;
        }

        public static Refund of(Long amount) {
            return Refund.builder().amount(amount).build();
        }
    }
}
