package com.github.gokid96.e_commerce.coupon.infrastructure.jpa;

import com.github.gokid96.e_commerce.coupon.domain.UserCoupon;
import com.github.gokid96.e_commerce.coupon.domain.UserCouponUsedStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {
    List<UserCoupon> findByUserIdAndUsedStatusIn(Long userId, List<UserCouponUsedStatus> usedStatuses);

    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    int countByCouponId(Long couponId);

    List<UserCoupon> findByCouponId(Long couponId);
}
