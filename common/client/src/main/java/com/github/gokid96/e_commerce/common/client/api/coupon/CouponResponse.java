package com.github.gokid96.e_commerce.common.client.api.coupon;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponResponse {

    @Getter
    @NoArgsConstructor
    public static class UserCoupon {
        private Long userCouponId;
        private Long couponId;
        private String couponName;
        private double discountRate;
        private LocalDateTime issuedAt;
    }
}
