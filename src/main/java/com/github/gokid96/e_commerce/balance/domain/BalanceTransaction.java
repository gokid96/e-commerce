package com.github.gokid96.e_commerce.balance.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_id")
    private Balance balance;

    private long amount;

    @Enumerated(EnumType.STRING)
    private BalanceTransactionType type;

    @Builder
    private BalanceTransaction(Balance balance, long amount, BalanceTransactionType type) {
        this.balance = balance;
        this.amount = amount;
        this.type = type;
    }

    public static BalanceTransaction ofCharge(Balance balance, long amount) {
        return BalanceTransaction.builder()
                .balance(balance)
                .amount(amount)        // +amount (충전은 양수)
                .type(BalanceTransactionType.CHARGE)
                .build();
    }

    public static BalanceTransaction ofUse(Balance balance, long amount) {
        return BalanceTransaction.builder()
                .balance(balance)
                .amount(-amount)       // -amount (사용은 음수)
                .type(BalanceTransactionType.USE)
                .build();
    }

    public static BalanceTransaction ofRefund(Balance balance, long amount) {
        return BalanceTransaction.builder()
                .balance(balance)
                .amount(amount)        // +amount (환불은 양수)
                .type(BalanceTransactionType.REFUND)
                .build();
    }
}