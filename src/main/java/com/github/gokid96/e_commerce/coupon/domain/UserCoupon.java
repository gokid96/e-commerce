package com.github.gokid96.e_commerce.coupon.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long couponId;

    @Enumerated(EnumType.STRING)
    private UserCouponUsedStatus usedStatus;

    @Builder
    private UserCoupon(Long id, Long userId, Long couponId, UserCouponUsedStatus usedStatus) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.usedStatus = usedStatus;
    }

    public static UserCoupon create(Long userId, Long couponId) {
        return UserCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .usedStatus(UserCouponUsedStatus.UNUSED)
                .build();
    }

    public void use() {
        if (usedStatus == UserCouponUsedStatus.USED) {
            throw new IllegalArgumentException("이미 사용된 쿠폰입니다.");
        }
        this.usedStatus = UserCouponUsedStatus.USED;
    }
}




