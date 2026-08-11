package com.github.gokid96.e_commerce.common.client.api.coupon;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Use {
        private Long userCouponId;

        @Builder
        private Use(Long userCouponId) {
            this.userCouponId = userCouponId;
        }

        public static Use of(Long userCouponId) {
            return Use.builder().userCouponId(userCouponId).build();
        }
    }
}
