package com.github.gokid96.e_commerce.balance.domain;

public interface BalanceClient {

    BalanceInfo.User getUser(Long userId);
}
