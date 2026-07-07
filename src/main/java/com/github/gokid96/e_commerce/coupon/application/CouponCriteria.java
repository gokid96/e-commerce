package com.github.gokid96.e_commerce.coupon.application;

import com.github.gokid96.e_commerce.coupon.domain.CouponCommand;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponCriteria {

    @Getter
    public static class PublishRequest {
        private final Long userId;
        private final Long couponId;

        @Builder
        private PublishRequest(Long userId, Long couponId) {
            this.userId = userId;
            this.couponId = couponId;
        }

        public static PublishRequest of(Long userId, Long couponId) {
            return PublishRequest.builder().userId(userId).couponId(couponId).build();
        }

        public CouponCommand.PublishRequest toCommand(LocalDateTime dateTime) {
            return CouponCommand.PublishRequest.of(userId, couponId, dateTime);
        }
    }

    @Getter
    public static class Publish {
        private final int maxPublishCount;

        private Publish(int maxPublishCount) {
            this.maxPublishCount = maxPublishCount;
        }

        public static Publish of(int maxPublishCount) {
            return new Publish(maxPublishCount);
        }

        public CouponCommand.Publish toCommand(Long couponId, int quantity) {
            return CouponCommand.Publish.of(couponId, quantity, maxPublishCount);
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
