package com.github.gokid96.e_commerce.balance.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;

    @Transactional
    public void chargeBalance(BalanceCommand.Charge command) {
        Optional<Balance> optionalBalance = balanceRepository.findOptionalByUserId(command.getUserId());

        optionalBalance.ifPresentOrElse(
                balance -> balance.charge(command.getAmount()),
                () -> balanceRepository.save(Balance.create(command.getUserId(), command.getAmount()))
        );
    }

    @Transactional
    public void useBalance(BalanceCommand.Use command) {
        Balance balance = balanceRepository.findOptionalByUserId(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("잔액이 존재하지 않습니다."));

        balance.use(command.getAmount());
    }

    public BalanceInfo.Balance getBalance(Long userId) {
        return balanceRepository.findOptionalByUserId(userId)
                .map(BalanceInfo.Balance::of)
                .orElseGet(() -> BalanceInfo.Balance.builder()
                        .userId(userId)
                        .amount(0L)
                        .build());
    }
}