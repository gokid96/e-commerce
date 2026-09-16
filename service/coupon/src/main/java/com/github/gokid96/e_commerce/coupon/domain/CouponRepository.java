package com.github.gokid96.e_commerce.coupon.domain;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface CouponRepository {

    // Coupon
    Optional<Coupon> findCouponById(Long couponId);
    Optional<Coupon> findWithLockById(Long couponId);
    List<Coupon> findCouponsByStatus(CouponStatus status);

    Coupon saveCoupon(Coupon coupon);

    // UserCoupon
    Optional<UserCoupon> findUserCouponById(Long userCouponId);

    UserCoupon saveUserCoupon(UserCoupon userCoupon);

    /** 쿠폰 정보를 조인한 조회 결과를 반환한다. */
    List<CouponInfo.UserCoupon> findUserCouponInfosByUserIdAndUsedStatusIn(Long userId, List<UserCouponUsedStatus> usedStatuses);

    UserCoupon findUserCouponByUserIdAndCouponId(Long userId, Long couponId);

    Optional<UserCoupon> findOptionalUserCouponByUserIdAndCouponId(Long userId, Long couponId);

    List<UserCoupon> findUserCouponsByCouponId(Long couponId);

    // 쿠폰 발급 가능 여부 플래그 (Redis)
    boolean findPublishableCouponById(Long couponId);

    void updateAvailableCoupon(Long couponId, boolean available);

}
