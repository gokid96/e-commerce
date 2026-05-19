package com.github.gokid96.e_commerce.balance.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private long amount;

    @OneToMany(mappedBy = "balance", cascade = CascadeType.ALL)
    private List<BalanceTransaction> balanceTransactions = new ArrayList<>();

    @Builder
    private Balance(Long id, Long userId, long amount) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        addChargeTransaction(amount);
    }

    public static Balance create(Long userId, Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 양수여야 합니다.");
        }
        return Balance.builder()
                .userId(userId)
                .amount(amount)
                .build();
    }

    public void charge(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 양수여야 합니다.");
        }
        this.amount += amount;
        addChargeTransaction(amount);
    }

    public void use(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용 금액은 양수여야 합니다.");
        }
        if (this.amount < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.amount -= amount;
        balanceTransactions.add(BalanceTransaction.ofUse(this, amount));
    }

    private void addChargeTransaction(long amount) {
        balanceTransactions.add(BalanceTransaction.ofCharge(this, amount));
    }
}