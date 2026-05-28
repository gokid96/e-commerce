package com.github.gokid96.e_commerce.coupon.interfaces.dto;

import com.github.gokid96.e_commerce.coupon.application.CouponCriteria;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponRequest {


    @Getter
    @NoArgsConstructor
    public static class Issue {
        @NotNull(message = "쿠폰 ID는 필수입니다.")
        @Positive(message = "쿠폰 ID는 양수여야 합니다.")
        private Long couponId;

        private Issue(Long couponId) {
            this.couponId = couponId;
        }

        public static Issue of(Long couponId) {
            return new Issue(couponId);
        }

        public CouponCriteria.Issue toCriteria(Long userId) {
            return CouponCriteria.Issue.of(userId, this.couponId);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Use {
        @NotNull(message = "사용자 쿠폰 ID는 필수입니다.")
        @Positive(message = "사용자 쿠폰 ID는 양수여야 합니다.")
        private Long userCouponId;

        private Use(Long userCouponId) {
            this.userCouponId = userCouponId;
        }

        public static Use of(Long userCouponId) {
            return new Use(userCouponId);
        }

        public CouponCriteria.Use toCriteria(Long userId) {
            return CouponCriteria.Use.of(userId, this.userCouponId);
        }
    }
}
