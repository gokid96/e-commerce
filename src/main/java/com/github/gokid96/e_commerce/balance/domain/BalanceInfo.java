package com.github.gokid96.e_commerce.balance.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceInfo {

    @Getter
    public static class Balance {
        private final Long userId;
        private final long amount;

        @Builder
        private Balance(final Long userId, final long amount) {
            this.userId = userId;
            this.amount = amount;
        }
        public static Balance of(com.github.gokid96.e_commerce.balance.domain.Balance balance){
            return Balance.builder()
                    .userId(balance.getUserId())
                    .amount(balance.getAmount())
                    .build();
        }

    }

}
