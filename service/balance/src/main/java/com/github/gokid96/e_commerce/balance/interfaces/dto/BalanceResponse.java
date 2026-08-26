package com.github.gokid96.e_commerce.balance.interfaces.dto;

import com.github.gokid96.e_commerce.balance.application.BalanceResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceResponse {

    @Getter
    public static class Balance {
        private final long amount;

        @Builder
        private Balance(long amount) {
            this.amount = amount;
        }

        public static Balance of(BalanceResult.Balance result) {
            return Balance.builder()
                    .amount(result.getAmount())
                    .build();
        }
    }

}
