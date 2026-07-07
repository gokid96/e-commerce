package com.github.gokid96.e_commerce.coupon.application;

import com.github.gokid96.e_commerce.coupon.domain.CouponInfo;
import com.github.gokid96.e_commerce.coupon.domain.UserCouponUsedStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponResult {

    @Getter
    public static class UserCoupon{
        private final Long userCouponId;
        private final Long couponId;
        private final String couponName;
        private final double discountRate;
        private final UserCouponUsedStatus usedStatus;

        @Builder
        private UserCoupon(Long userCouponId,Long couponId,String couponName,double discountRate,UserCouponUsedStatus usedStatus) {
            this.userCouponId = userCouponId;
            this.couponId = couponId;
            this.couponName = couponName;
            this.discountRate = discountRate;
            this.usedStatus = usedStatus;
        }
        public static UserCoupon of(CouponInfo.UserCoupon info){
            return UserCoupon.builder()
                    .couponId(info.getCouponId())
                    .couponName(info.getCouponName())
                    .discountRate(info.getDiscountRate())
                    .usedStatus(info.getUsedStatus())
                    .userCouponId(info.getUserCouponId())
                    .build();
        }

    }


}
