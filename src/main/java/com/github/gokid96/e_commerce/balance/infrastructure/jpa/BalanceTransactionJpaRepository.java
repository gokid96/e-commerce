package com.github.gokid96.e_commerce.balance.infrastructure.jpa;

import com.github.gokid96.e_commerce.balance.domain.BalanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceTransactionJpaRepository extends JpaRepository<BalanceTransaction, Long> {
}