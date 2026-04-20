package com.github.gokid96.e_commerce.balance.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class BalanceChargeRequest {
    @NotNull(message = "충전 금액은 필수입니다.")
    @Positive(message = "충전 금액은 양수여야 합니다.")
    private Long amount;

    private BalanceChargeRequest(long amount) {
        this.amount = amount;
    }
    public static BalanceChargeRequest of(long amount) {
        return new BalanceChargeRequest(amount);
    }
}
