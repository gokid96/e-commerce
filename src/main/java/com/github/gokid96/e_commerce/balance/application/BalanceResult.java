package com.github.gokid96.e_commerce.balance.application;

import com.github.gokid96.e_commerce.balance.domain.BalanceInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceResult {
    @Getter
    public static class Balance{
        private final long amount;

        @Builder
        private Balance(long amount) {
            this.amount = amount;
        }

        public static Balance of(BalanceInfo.Balance info){
            return Balance.builder()
                    .amount(info.getAmount())
                    .build();
        }
    }
}