package com.github.gokid96.e_commerce.balance.application;

import com.github.gokid96.e_commerce.balance.domain.BalanceCommand;
import lombok.Builder;
import lombok.Getter;

public class BalanceCriteria {

    @Getter
    public static class Charge{
        private final Long userId;
        private final Long amount;

        @Builder
        private Charge(Long userId, Long amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public static Charge of(Long userId, Long amount) {
            return builder()
                    .userId(userId)
                    .amount(amount)
                    .build();
        }

        public BalanceCommand.Charge toCommand() {
            return BalanceCommand.Charge.of(this.userId, this.amount);
        }

    }
}