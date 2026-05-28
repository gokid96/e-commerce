package com.github.gokid96.e_commerce.coupon.application;

import com.github.gokid96.e_commerce.coupon.domain.CouponInfo;
import com.github.gokid96.e_commerce.coupon.domain.CouponService;
import com.github.gokid96.e_commerce.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponFacade {
    private final UserService userService;
    private final CouponService couponService;

    public void issueCoupon(CouponCriteria.Issue criteria) {
        userService.getUser(criteria.getUserId());
        couponService.issueCoupon(criteria.toCommand());
    }

    public void useCoupon(CouponCriteria.Use criteria) {
        userService.getUser(criteria.getUserId());
        couponService.useCoupon(criteria.toCommand());
    }

    public List<CouponResult.UserCoupon> getUserCoupons(Long userId) {
        userService.getUser(userId);
        List<CouponInfo.UserCoupon> infos = couponService.getUserCoupons(userId);

        return infos.stream()
                .map(CouponResult.UserCoupon::of)
                .toList();
    }
}
