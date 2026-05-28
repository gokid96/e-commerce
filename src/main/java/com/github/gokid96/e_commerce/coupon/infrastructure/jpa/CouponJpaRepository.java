package com.github.gokid96.e_commerce.coupon.infrastructure.jpa;

import com.github.gokid96.e_commerce.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
