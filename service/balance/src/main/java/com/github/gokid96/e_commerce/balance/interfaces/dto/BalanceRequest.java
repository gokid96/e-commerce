package com.github.gokid96.e_commerce.balance.interfaces.dto;

import com.github.gokid96.e_commerce.balance.application.BalanceCriteria;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceRequest {

    @Getter
    @NoArgsConstructor
    public static class Charge {
        @NotNull(message = "충전 금액은 필수입니다.")
        @Positive(message = "충전 금액은 양수여야 합니다.")
        private Long amount;

        public BalanceCriteria.Charge toCriteria(Long userId) {
            return BalanceCriteria.Charge.of(userId, this.amount);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Use {
        @NotNull(message = "사용 금액은 필수입니다.")
        @Positive(message = "사용 금액은 양수여야 합니다.")
        private Long amount;
    }

    @Getter
    @NoArgsConstructor
    public static class Refund {
        @NotNull(message = "환불 금액은 필수입니다.")
        @Positive(message = "환불 금액은 양수여야 합니다.")
        private Long amount;
    }
}