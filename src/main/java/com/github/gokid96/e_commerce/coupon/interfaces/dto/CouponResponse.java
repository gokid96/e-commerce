package com.github.gokid96.e_commerce.coupon.interfaces.dto;

import com.github.gokid96.e_commerce.coupon.application.CouponResult;
import com.github.gokid96.e_commerce.coupon.domain.UserCouponUsedStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResponse {

    @Getter
    public static class UserCoupon {
        private final Long userCouponId;
        private final Long couponId;
        private final String couponName;
        private final long discountAmount;
        private final UserCouponUsedStatus usedStatus;

        @Builder
        private UserCoupon(Long userCouponId, Long couponId, String couponName, long discountAmount, UserCouponUsedStatus usedStatus) {
            this.userCouponId = userCouponId;
            this.couponId = couponId;
            this.couponName = couponName;
            this.discountAmount = discountAmount;
            this.usedStatus = usedStatus;
        }

        public static UserCoupon of(CouponResult.UserCoupon result) {
            return UserCoupon.builder()
                    .userCouponId(result.getUserCouponId())
                    .couponId(result.getCouponId())
                    .couponName(result.getCouponName())
                    .discountAmount(result.getDiscountAmount())
                    .usedStatus(result.getUsedStatus())
                    .build();
        }

    }

}
