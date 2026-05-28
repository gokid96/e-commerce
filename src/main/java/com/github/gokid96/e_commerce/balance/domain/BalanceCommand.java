package com.github.gokid96.e_commerce.balance.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceCommand {

    @Getter
    public static class Charge {
        private final Long userId;
        private final Long amount;

        @Builder
        private Charge(final Long userId, final Long amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public static Charge of(Long userId, Long amount) {
            return Charge.builder()
                    .userId(userId)
                    .amount(amount)
                    .build();
        }
    }

    @Getter
    public static class Use {
        private final Long userId;
        private final Long amount;

        @Builder
        private Use(final Long userId, final Long amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public static Use of(Long userId, Long amount) {
            return Use.builder()
                    .userId(userId)
                    .amount(amount)
                    .build();
        }

    }


}
