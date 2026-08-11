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
                com.github.gokid96.e_commerce.coupon.domain.Coupon coupon
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

    @Getter
    public static class Coupon {
        private final Long couponId;
        private final String couponName;
        private final double discountRate;

        @Builder
        private Coupon(Long couponId, String couponName, double discountRate) {
            this.couponId = couponId;
            this.couponName = couponName;
            this.discountRate = discountRate;
        }

        public static Coupon of(com.github.gokid96.e_commerce.coupon.domain.Coupon coupon) {
            return Coupon.builder()
                    .couponId(coupon.getId())
                    .couponName(coupon.getName())
                    .discountRate(coupon.getDiscountRate())
                    .build();
        }
    }
    @Getter
    public static class UsableCoupon {
        private final Long userCouponId;

        @Builder
        private UsableCoupon(Long userCouponId) {
            this.userCouponId = userCouponId;
        }

        public static UsableCoupon of(Long userCouponId) {
            return UsableCoupon.builder()
                    .userCouponId(userCouponId)
                    .build();
        }
    }

    @Getter
    public static class User {
        private final Long userId;
        private final String nickname;

        @Builder
        private User(Long userId, String nickname) {
            this.userId = userId;
            this.nickname = nickname;
        }

        public static User of(Long userId, String nickname) {
            return User.builder()
                    .userId(userId)
                    .nickname(nickname)
                    .build();
        }
    }
}
