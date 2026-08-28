package com.github.gokid96.e_commerce.coupon.application;

import com.github.gokid96.e_commerce.coupon.domain.CouponClient;
import com.github.gokid96.e_commerce.coupon.domain.CouponInfo;
import com.github.gokid96.e_commerce.coupon.domain.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponFacade {
    private final CouponClient couponClient;
    private final CouponService couponService;

    // 발급 요청 접수: 발급 가능 여부만 확인하고 Kafka(Outbox)로 발급 요청 이벤트 발행
    public void requestPublishUserCoupon(CouponCriteria.PublishRequest criteria) {
        couponService.requestPublishUserCoupon(criteria.toCommand());
    }

    public void useCoupon(CouponCriteria.Use criteria) {
        couponClient.getUser(criteria.getUserId());
        couponService.useCoupon(criteria.toCommand());
    }

    public void cancelUserCoupon(Long userCouponId) {
        couponService.cancelUserCoupon(userCouponId);
    }

    public void useUserCoupon(Long userCouponId) {
        couponService.useUserCoupon(userCouponId);
    }

    public List<CouponResult.UserCoupon> getUserCoupons(Long userId) {
        couponClient.getUser(userId);
        List<CouponInfo.UserCoupon> infos = couponService.getUserCoupons(userId);

        return infos.stream()
                .map(CouponResult.UserCoupon::of)
                .toList();
    }

    public CouponInfo.UserCoupon getUsableUserCoupon(Long userCouponId) {
        return couponService.getUsableUserCoupon(userCouponId);
    }
}
