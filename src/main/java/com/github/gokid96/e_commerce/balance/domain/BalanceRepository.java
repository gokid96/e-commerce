package com.github.gokid96.e_commerce.balance.domain;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository {
    Optional<Balance> findOptionalByUserId(Long userId);
    Balance save(Balance balance);
    BalanceTransaction saveTransaction(BalanceTransaction balanceTransaction);
}