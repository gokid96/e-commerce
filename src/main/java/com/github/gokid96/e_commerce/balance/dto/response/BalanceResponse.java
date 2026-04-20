package com.github.gokid96.e_commerce.balance.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceResponse {
    private long amount;

    public static BalanceResponse of(long amount) {
        return new BalanceResponse(amount);
    }
}
