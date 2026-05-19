package com.github.gokid96.e_commerce.balance.infrastructure;

import com.github.gokid96.e_commerce.balance.domain.Balance;
import com.github.gokid96.e_commerce.balance.domain.BalanceRepository;
import com.github.gokid96.e_commerce.balance.infrastructure.jpa.BalanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BalanceCoreRepository implements BalanceRepository {

    private final BalanceJpaRepository balanceJpaRepository;

    @Override
    public Optional<Balance> findOptionalByUserId(Long userId) {
        return balanceJpaRepository.findByUserId(userId);
    }

    @Override
    public Balance save(Balance balance) {
        return balanceJpaRepository.save(balance);
    }
}