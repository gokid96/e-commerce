package com.github.gokid96.e_commerce.coupon.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public void issueCoupon(CouponCommand.Issue command) {
        Coupon coupon = couponRepository.findCouponById(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        coupon.issue();
        couponRepository.saveCoupon(coupon);

        UserCoupon userCoupon = UserCoupon.create(command.getUserId(), command.getCouponId());
        couponRepository.saveUserCoupon(userCoupon);
    }

    public void useCoupon(CouponCommand.Use command) {
        UserCoupon userCoupon = couponRepository.findUserCouponById(command.getUserCouponId())
                .orElseThrow(() -> new IllegalArgumentException("발급된 쿠폰이 존재하지 않습니다."));

        if (!userCoupon.getUserId().equals(command.getUserId())) {
            throw new IllegalArgumentException("본인의 쿠폰이 아닙니다.");
        }

        userCoupon.use();
        couponRepository.saveUserCoupon(userCoupon);
    }

    public List<CouponInfo.UserCoupon> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = couponRepository.findUserCouponsByUserId(userId);

        return userCoupons.stream()
                .map(userCoupon -> {
                    Coupon coupon = couponRepository.findCouponById(userCoupon.getCouponId())
                            .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));
                    return CouponInfo.UserCoupon.of(userCoupon, coupon);
                })
                .toList();

    }


}







