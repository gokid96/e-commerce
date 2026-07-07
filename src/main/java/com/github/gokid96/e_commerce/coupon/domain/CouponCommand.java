package com.github.gokid96.e_commerce.coupon.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponCommand {

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

    // 임포트: java.time.LocalDateTime

    @Getter
    public static class PublishRequest {
        private final Long userId;
        private final Long couponId;
        private final LocalDateTime issuedAt;

        @Builder
        private PublishRequest(Long userId, Long couponId, LocalDateTime issuedAt) {
            this.userId = userId;
            this.couponId = couponId;
            this.issuedAt = issuedAt;
        }

        public static PublishRequest of(Long userId, Long couponId, LocalDateTime issuedAt) {
            return PublishRequest.builder().userId(userId).couponId(couponId).issuedAt(issuedAt).build();
        }
    }

    @Getter
    public static class Publish {
        private final Long couponId;
        private final int quantity;
        private final int maxPublishCount;

        @Builder
        private Publish(Long couponId, int quantity, int maxPublishCount) {
            this.couponId = couponId;
            this.quantity = quantity;
            this.maxPublishCount = maxPublishCount;
        }

        public static Publish of(Long couponId, int quantity, int maxPublishCount) {
            return Publish.builder().couponId(couponId).quantity(quantity).maxPublishCount(maxPublishCount).build();
        }
    }

    @Getter
    public static class PublishFinish {
        private final Long couponId;
        private final int quantity;

        @Builder
        private PublishFinish(Long couponId, int quantity) {
            this.couponId = couponId;
            this.quantity = quantity;
        }

        public static PublishFinish of(Long couponId, int quantity) {
            return PublishFinish.builder().couponId(couponId).quantity(quantity).build();
        }
    }

    @Getter
    public static class Candidates {
        private final Long couponId;
        private final int start;
        private final int end;

        @Builder
        private Candidates(Long couponId, int start, int end) {
            this.couponId = couponId;
            this.start = start;
            this.end = end;
        }

        public static Candidates of(Long couponId, int start, int end) {
            return Candidates.builder().couponId(couponId).start(start).end(end).build();
        }
    }
}
