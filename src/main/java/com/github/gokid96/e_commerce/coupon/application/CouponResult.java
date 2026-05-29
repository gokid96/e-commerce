package com.github.gokid96.e_commerce.coupon.application;

import com.github.gokid96.e_commerce.coupon.domain.CouponInfo;
import com.github.gokid96.e_commerce.coupon.domain.UserCouponUsedStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponResult {

    @Getter
    public static class UserCoupon{
        private final Long userCouponId;
        private final Long couponId;
        private final String couponName;
        private final long discountAmount;
        private final UserCouponUsedStatus usedStatus;

        @Builder
        private UserCoupon(Long userCouponId,Long couponId,String couponName,long discountAmount,UserCouponUsedStatus usedStatus) {
            this.userCouponId = userCouponId;
            this.couponId = couponId;
            this.couponName = couponName;
            this.discountAmount = discountAmount;
            this.usedStatus = usedStatus;
        }
        public static UserCoupon of(CouponInfo.UserCoupon info){
            return UserCoupon.builder()
                    .couponId(info.getCouponId())
                    .couponName(info.getCouponName())
                    .discountAmount(info.getDiscountAmount())
                    .usedStatus(info.getUsedStatus())
                    .userCouponId(info.getUserCouponId())
                    .build();
        }

    }


}
