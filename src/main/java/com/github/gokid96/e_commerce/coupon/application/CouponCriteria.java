package com.github.gokid96.e_commerce.coupon.application;

import com.github.gokid96.e_commerce.coupon.domain.CouponCommand;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponCriteria {

    @Getter
    public static class Issue {
        private final Long userId;
        private final Long couponId;

        @Builder
        private Issue(Long userId, Long couponId) {
            this.userId = userId;
            this.couponId = couponId;
        }

        public static Issue of(Long userId, Long couponId) {
            return Issue.builder()
                    .userId(userId)
                    .couponId(couponId)
                    .build();
        }

        public CouponCommand.Issue toCommand() {
            return CouponCommand.Issue.of(this.userId, this.couponId);
        }
    }

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
        public CouponCommand.Use toCommand() {
            return CouponCommand.Use.of(this.userId, this.userCouponId);
        }

    }


}
