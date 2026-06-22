package com.github.gokid96.e_commerce.coupon.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @Column(name = "coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double discountRate;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private LocalDateTime expiredAt;

    @Builder
    private Coupon(Long id, String name, double discountRate, int quantity, CouponStatus status, LocalDateTime expiredAt) {
        this.id = id;
        this.name = name;
        this.discountRate = discountRate;
        this.quantity = quantity;
        this.status = status;
        this.expiredAt = expiredAt;
    }

    public static Coupon create(String name, double discountRate, int quantity, CouponStatus status, LocalDateTime expiredAt) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("쿠폰 이름은 필수입니다.");
        }
        if (discountRate < 0 || discountRate > 1) {
            throw new IllegalArgumentException("할인율은 0과 1 사이여야 합니다.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("쿠폰 수량은 0 이상이어야 합니다.");
        }
        if (status == null) {
            throw new IllegalArgumentException("쿠폰 상태는 필수입니다.");
        }
        if (expiredAt == null || expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("쿠폰 만료일은 현재 시간 이후여야 합니다.");
        }
        return Coupon.builder()
                .name(name)
                .discountRate(discountRate)
                .quantity(quantity)
                .status(status)
                .expiredAt(expiredAt)
                .build();
    }

    public void issue() {
        if (status.cannotPublishable()) {
            throw new IllegalStateException("발급 불가능한 쿠폰입니다.");
        }
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("쿠폰이 만료되었습니다.");
        }
        if (quantity <= 0) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }
        this.quantity--;
    }

}

