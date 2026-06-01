package com.github.gokid96.e_commerce.coupon.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponInfo {

    @Getter
    public static class UserCoupon {
        private final Long userCouponId;
        private final Long couponId;
        private final String couponName;
        private final double discountRate;
        private final UserCouponUsedStatus usedStatus;

        @Builder
        private UserCoupon(Long userCouponId, Long couponId, String couponName,
                           double discountRate, UserCouponUsedStatus usedStatus) {
            this.userCouponId = userCouponId;
            this.couponId = couponId;
            this.couponName = couponName;
            this.discountRate = discountRate;
            this.usedStatus = usedStatus;
        }

        public static UserCoupon of(
                com.github.gokid96.e_commerce.coupon.domain.UserCoupon userCoupon,
                Coupon coupon
        ) {
            return UserCoupon.builder()
                    .userCouponId(userCoupon.getId())
                    .couponId(coupon.getId())
                    .couponName(coupon.getName())
                    .discountRate(coupon.getDiscountRate())
                    .usedStatus(userCoupon.getUsedStatus())
                    .build();
        }

    }
}
