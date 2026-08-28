package com.github.gokid96.e_commerce.coupon.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponEventPublisher couponEventPublisher;


    public void useCoupon(CouponCommand.Use command) {
        UserCoupon userCoupon = couponRepository.findUserCouponById(command.getUserCouponId())
                .orElseThrow(() -> new IllegalArgumentException("발급된 쿠폰이 존재하지 않습니다."));

        if (!userCoupon.getUserId().equals(command.getUserId())) {
            throw new IllegalArgumentException("본인의 쿠폰이 아닙니다.");
        }

        userCoupon.use();
        couponRepository.saveUserCoupon(userCoupon);
    }

    public CouponInfo.UsableCoupon getUsableCoupon(CouponCommand.UsableCoupon command) {
        UserCoupon userCoupon = couponRepository.findUserCouponByUserIdAndCouponId(
                command.getUserId(), command.getCouponId());

        if (userCoupon.cannotUse()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }

        return CouponInfo.UsableCoupon.of(userCoupon.getId());
    }

    public CouponInfo.UserCoupon getUsableUserCoupon(Long userCouponId) {
        UserCoupon userCoupon = couponRepository.findUserCouponById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        if (userCoupon.cannotUse()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }

        Coupon coupon = couponRepository.findCouponById(userCoupon.getCouponId())
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        return CouponInfo.UserCoupon.of(userCoupon, coupon);
    }

    public CouponInfo.Coupon getCoupon(Long couponId) {
        Coupon coupon = couponRepository.findCouponById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));
        return CouponInfo.Coupon.of(coupon);
    }

    public void useUserCoupon(Long userCouponId) {
        UserCoupon userCoupon = couponRepository.findUserCouponById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("발급된 쿠폰이 존재하지 않습니다."));
        userCoupon.use();
        couponRepository.saveUserCoupon(userCoupon);
    }

    public void cancelUserCoupon(Long userCouponId) {
        UserCoupon userCoupon = couponRepository.findUserCouponById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("발급된 쿠폰이 존재하지 않습니다."));
        userCoupon.cancel();
        couponRepository.saveUserCoupon(userCoupon);
    }

    public List<CouponInfo.UserCoupon> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = couponRepository.findUserCouponsByUserIdAndUsedStatusIn(
                userId, UserCouponUsedStatus.forUsable());

        return userCoupons.stream()
                .map(userCoupon -> {
                    Coupon coupon = couponRepository.findCouponById(userCoupon.getCouponId())
                            .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));
                    return CouponInfo.UserCoupon.of(userCoupon, coupon);
                })
                .toList();
    }

    public void requestPublishUserCoupon(CouponCommand.Publish command) {
        boolean publishable = couponRepository.findPublishableCouponById(command.getCouponId());

        if (!publishable) {
            throw new IllegalArgumentException("발급 불가한 쿠폰입니다.");
        }

        CouponEvent.PublishRequested event =
                CouponEvent.PublishRequested.of(command.getUserId(), command.getCouponId());
        couponEventPublisher.publishRequested(event);
    }

    @Transactional
    public void publishUserCoupon(CouponCommand.Publish command) {
        couponRepository.findOptionalUserCouponByUserIdAndCouponId(command.getUserId(), command.getCouponId())
                .ifPresent(userCoupon -> {
                    throw new IllegalArgumentException("이미 발급된 쿠폰입니다.");
                });

        Coupon coupon = couponRepository.findCouponById(command.getCouponId())
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));
        coupon.publish();
        couponRepository.saveCoupon(coupon);

        UserCoupon userCoupon = UserCoupon.create(command.getUserId(), command.getCouponId());
        couponRepository.saveUserCoupon(userCoupon);

        CouponEvent.Published event = CouponEvent.Published.of(coupon);
        couponEventPublisher.published(event);
    }

    @Transactional(readOnly = true)
    public void stopPublishCoupon(Long couponId) {
        Coupon coupon = couponRepository.findCouponById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        if (coupon.isNotPublishable()) {
            couponRepository.updateAvailableCoupon(couponId, false);
        }
    }
}