package com.github.gokid96.e_commerce.coupon.application;

import com.github.gokid96.e_commerce.coupon.domain.CouponCommand;
import com.github.gokid96.e_commerce.coupon.domain.CouponInfo;
import com.github.gokid96.e_commerce.coupon.domain.CouponService;
import com.github.gokid96.e_commerce.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponFacade {
    private final UserService userService;
    private final CouponService couponService;

    // 발급 요청 접수: Redis ZSET에 원자적으로 기록만 하고 즉시 응답
    public void requestPublishUserCoupon(CouponCriteria.PublishRequest criteria) {
        couponService.requestPublishUserCoupon(criteria.toCommand(LocalDateTime.now()));
    }

    // 스케줄러용: 발급 가능 쿠폰별로 후보를 꺼내 실제 발급
    @Transactional
    public void publishUserCoupons(CouponCriteria.Publish criteria) {
        CouponInfo.PublishableCoupons coupons = couponService.getPublishableCoupons();

        coupons.getCoupons().stream()
                .map(c -> criteria.toCommand(c.getCouponId(), c.getQuantity()))
                .forEach(couponService::publishUserCoupons);
    }

    // 스케줄러용: 수량이 소진된 쿠폰을 발급 종료 처리
    @Transactional
    public void finishedPublishCoupons() {
        CouponInfo.PublishableCoupons coupons = couponService.getPublishableCoupons();

        coupons.getCoupons().stream()
                .map(c -> CouponCommand.PublishFinish.of(c.getCouponId(), c.getQuantity()))
                .filter(couponService::isPublishFinished)
                .forEach(c -> couponService.finishCoupon(c.getCouponId()));
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
