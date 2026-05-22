package com.github.gokid96.e_commerce.coupon.domain;

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

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private long discountAmount;
    private int totalQuantity;  // 총 발급 가능 수량
    private int issuedQuantity; // 현재까지 발급된 수량

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    @Builder
    private Coupon(Long id, String name, long discountAmount, int totalQuantity) {
        this.id = id;
        this.name = name;
        this.discountAmount = discountAmount;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = 0;
        this.status = CouponStatus.AVAILABLE;
    }

    public static Coupon create(String name, long discountAmount, int totalQuantity) {
        if (discountAmount < 0) {
            throw new IllegalArgumentException("할인 금액은 양수여야 합니다.");
        }
        if (totalQuantity <= 0) {
            throw new IllegalArgumentException("발급 수량은 양수여야 합니다.");
        }
        return Coupon.builder()
                .name(name)
                .discountAmount(discountAmount)
                .totalQuantity(totalQuantity)
                .build();

    }

    public void issue() {
        if (status == CouponStatus.UNAVAILABLE) {
            throw new IllegalArgumentException("발급 불가능한 쿠폰입니다.");
        }
        if (issuedQuantity >= totalQuantity) {
            this.status = CouponStatus.UNAVAILABLE;
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }
        this.issuedQuantity++;
        if (issuedQuantity >= totalQuantity) {
            this.status = CouponStatus.UNAVAILABLE;
        }
    }

}

