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

    // 발급 요청 접수: 발급 가능 여부만 확인하고 Kafka(Outbox)로 발급 요청 이벤트 발행
    public void requestPublishUserCoupon(CouponCriteria.PublishRequest criteria) {
        couponService.requestPublishUserCoupon(criteria.toCommand());
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
