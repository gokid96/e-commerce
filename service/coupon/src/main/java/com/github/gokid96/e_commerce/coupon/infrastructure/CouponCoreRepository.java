package com.github.gokid96.e_commerce.coupon.infrastructure;

import com.github.gokid96.e_commerce.coupon.domain.Coupon;
import com.github.gokid96.e_commerce.coupon.domain.CouponRepository;
import com.github.gokid96.e_commerce.coupon.domain.CouponStatus;
import com.github.gokid96.e_commerce.coupon.domain.UserCoupon;
import com.github.gokid96.e_commerce.coupon.domain.UserCouponUsedStatus;
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
    private final UserCouponRedisRepository userCouponRedisRepository;
    /*
     * coupon
     * */
    @Override
    public Optional<Coupon> findCouponById(Long couponId) {
        return couponJpaRepository.findById(couponId);
    }

    @Override
    public Optional<Coupon> findWithLockById(Long couponId) {
        return couponJpaRepository.findWithLockById(couponId);
    }

    @Override
    public Coupon saveCoupon(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public List<Coupon> findCouponsByStatus(CouponStatus status) {
        return couponJpaRepository.findByStatus(status);
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
    public List<UserCoupon> findUserCouponsByUserIdAndUsedStatusIn(Long userId, List<UserCouponUsedStatus> usedStatuses) {
        return userCouponJpaRepository.findByUserIdAndUsedStatusIn(userId, usedStatuses);
    }

    @Override
    public UserCoupon findUserCouponByUserIdAndCouponId(Long userId, Long couponId) {
        return userCouponJpaRepository.findByUserIdAndCouponId(userId, couponId).orElseThrow(() -> new IllegalArgumentException("발급된 쿠폰이 존재하지 않습니다."));
    }

    @Override
    public Optional<UserCoupon> findOptionalUserCouponByUserIdAndCouponId(Long userId, Long couponId) {
        return userCouponJpaRepository.findByUserIdAndCouponId(userId, couponId);
    }

    @Override
    public List<UserCoupon> findUserCouponsByCouponId(Long couponId) {
        return userCouponJpaRepository.findByCouponId(couponId);
    }

    @Override
    public boolean findPublishableCouponById(Long couponId) {
        return userCouponRedisRepository.findPublishableCouponById(couponId);
    }

    @Override
    public void updateAvailableCoupon(Long couponId, boolean available) {
        userCouponRedisRepository.updateAvailable(couponId, available);
    }
}
