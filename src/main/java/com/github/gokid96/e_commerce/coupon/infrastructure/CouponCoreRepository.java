package com.github.gokid96.e_commerce.coupon.infrastructure;

import com.github.gokid96.e_commerce.coupon.domain.Coupon;
import com.github.gokid96.e_commerce.coupon.domain.CouponRepository;
import com.github.gokid96.e_commerce.coupon.domain.UserCoupon;
import com.github.gokid96.e_commerce.coupon.infrastructure.jpa.CouponJpaRepository;
import com.github.gokid96.e_commerce.coupon.infrastructure.jpa.UserCouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CouponCoreRepository implements CouponRepository {
    private final CouponJpaRepository couponJpaRepository;
    private final UserCouponJpaRepository userCouponJpaRepository;

    /*
    * coupon
    * */
    @Override
    public Optional<Coupon> findCouponById(Long couponId) {
        return couponJpaRepository.findById(couponId);
    }

    @Override
    public Coupon saveCoupon(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }
    /*
    * userCoupon
    * */
    @Override
    public Optional<UserCoupon> findUserCouponById(Long couponId) {
        return userCouponJpaRepository.findById(couponId);
    }

    @Override
    public UserCoupon saveUserCoupon(UserCoupon userCoupon) {
        return userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public List<UserCoupon> findUserCouponsByUserId(Long userId) {
        return userCouponJpaRepository.findAllByUserId(userId);
    }

}
