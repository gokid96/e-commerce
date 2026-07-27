package com.github.gokid96.e_commerce.balance.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "balance", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private long amount;

    @Version
    private Integer version;

    private static final long MAX_BALANCE_AMOUNT = 10_000_000L;

    @Builder
    private Balance(Long id, Long userId, long amount) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
    }

    public static Balance create(Long userId, Long amount) {
        validateAmount(amount);
        return Balance.builder()
                .userId(userId)
                .amount(amount)
                .build();
    }

    public void charge(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 양수여야 합니다.");
        }
        if (this.amount + amount > MAX_BALANCE_AMOUNT) {
            throw new IllegalArgumentException("최대 잔액(1,000만원)을 초과할 수 없습니다.");
        }
        this.amount += amount;
    }

    public void use(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용 금액은 양수여야 합니다.");
        }
        if (this.amount < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.amount -= amount;
    }

    public void refund(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("환불 금액은 양수여야 합니다.");
        }
        this.amount += amount;
    }

    private static void validateAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 양수여야 합니다.");
        }
        if (amount > MAX_BALANCE_AMOUNT) {
            throw new IllegalArgumentException("최대 잔액(1,000만원)을 초과할 수 없습니다.");
        }
    }
}