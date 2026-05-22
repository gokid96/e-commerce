package com.github.gokid96.e_commerce.coupon.infrastructure.jpa;

import com.github.gokid96.e_commerce.coupon.domain.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {
    List<UserCoupon> findAllByUserId(Long userId);
}
