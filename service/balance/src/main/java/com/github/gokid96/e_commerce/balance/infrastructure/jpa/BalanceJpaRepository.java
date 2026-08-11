package com.github.gokid96.e_commerce.balance.infrastructure.jpa;

import com.github.gokid96.e_commerce.balance.domain.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByUserId(Long userId);
}