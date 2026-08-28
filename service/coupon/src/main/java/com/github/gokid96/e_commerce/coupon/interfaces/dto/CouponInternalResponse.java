package com.github.gokid96.e_commerce.coupon.interfaces.dto;

import com.github.gokid96.e_commerce.coupon.domain.CouponInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponInternalResponse {

    @Getter
    public static class UsableCoupon {
        private final Long userCouponId;
        private final Long couponId;
        private final String couponName;
        private final double discountRate;

        private UsableCoupon(Long userCouponId, Long couponId, String couponName, double discountRate) {
            this.userCouponId = userCouponId;
            this.couponId = couponId;
            this.couponName = couponName;
            this.discountRate = discountRate;
        }

        public static UsableCoupon of(CouponInfo.UserCoupon info) {
            return new UsableCoupon(
                    info.getUserCouponId(),
                    info.getCouponId(),
                    info.getCouponName(),
                    info.getDiscountRate());
        }
    }
}