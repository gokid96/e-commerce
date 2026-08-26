package com.github.gokid96.e_commerce.coupon.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponCommand {

    @Getter
    public static class Use {
        private final Long userId;
        private final Long userCouponId;

        @Builder
        private Use(Long userId, Long userCouponId) {
            this.userId = userId;
            this.userCouponId = userCouponId;
        }

        public static Use of(Long userId, Long userCouponId) {
            return Use.builder()
                    .userId(userId)
                    .userCouponId(userCouponId)
                    .build();
        }
    }

    @Getter
    public static class UsableCoupon {
        private final Long userId;
        private final Long couponId;

        @Builder
        private UsableCoupon(Long userId, Long couponId) {
            this.userId = userId;
            this.couponId = couponId;
        }

        public static UsableCoupon of(Long userId, Long couponId) {
            return UsableCoupon.builder()
                    .couponId(couponId)
                    .userId(userId)
                    .build();
        }
    }

    @Getter
    public static class Publish {
        private final Long userId;
        private final Long couponId;

        @Builder
        private Publish(Long userId, Long couponId) {
            this.userId = userId;
            this.couponId = couponId;
        }

        public static Publish of(Long userId, Long couponId) {
            return Publish.builder().userId(userId).couponId(couponId).build();
        }
    }
}
