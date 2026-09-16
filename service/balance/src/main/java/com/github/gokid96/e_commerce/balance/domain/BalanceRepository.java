package com.github.gokid96.e_commerce.balance.domain;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface BalanceRepository {
    Optional<Balance> findOptionalByUserId(Long userId);
    Balance save(Balance balance);
    BalanceTransaction saveTransaction(BalanceTransaction balanceTransaction);
}