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

    @Getter
    public static class User {
        private final Long userId;
        private final String nickname;

        @Builder
        private User(final Long userId, final String nickname) {
            this.userId = userId;
            this.nickname = nickname;
        }

        public static User of(Long userId, String nickname) {
            return User.builder()
                    .userId(userId)
                    .nickname(nickname)
                    .build();
        }
    }

}
