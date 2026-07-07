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

    @Transactional
    public void issueCoupon(CouponCommand.Issue command) {
        Coupon coupon = couponRepository.findWithLockById(command.getCouponId())
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        couponRepository.findOptionalUserCouponByUserIdAndCouponId(command.getUserId(), command.getCouponId())
                .ifPresent(userCoupon -> {
                    throw new IllegalArgumentException("이미 발급된 쿠폰입니다.");
                });

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

    public CouponInfo.UsableCoupon getUsableCoupon(CouponCommand.UsableCoupon command) {
        UserCoupon userCoupon = couponRepository.findUserCouponByUserIdAndCouponId(
                command.getUserId(), command.getCouponId());

        if (userCoupon.cannotUse()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }

        return CouponInfo.UsableCoupon.of(userCoupon.getId());
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

    @Transactional(readOnly = true)
    public CouponInfo.PublishableCoupons getPublishableCoupons() {
        List<CouponInfo.PublishableCoupon> coupons =
                couponRepository.findCouponsByStatus(CouponStatus.PUBLISHABLE).stream()
                        .map(c -> CouponInfo.PublishableCoupon.of(c.getId(), c.getQuantity()))
                        .toList();
        return CouponInfo.PublishableCoupons.of(coupons);
    }

    @Transactional
    public void finishCoupon(Long couponId) {
        Coupon coupon = couponRepository.findCouponById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));
        coupon.finish();
        couponRepository.saveCoupon(coupon);
    }

    public boolean requestPublishUserCoupon(CouponCommand.PublishRequest command) {
        boolean success = couponRepository.savePublishRequest(command);

        if (!success) {
            throw new IllegalArgumentException("쿠폰 발급 요청에 실패했습니다.");
        }
        return true;
    }

    // 이미 발급된 수(start)부터 잔여 수량 한도 내에서 후보를 꺼내 실제 발급한다.
    public void publishUserCoupons(CouponCommand.Publish command) {
        int start = couponRepository.countUserCouponsByCouponId(command.getCouponId());
        int end = Math.min(command.getQuantity(), start + command.getMaxPublishCount());

        if (start >= command.getQuantity()) {
            log.info("발급할 쿠폰 수량이 없습니다. couponId: {}", command.getCouponId());
            return;
        }

        List<CouponInfo.Candidates> candidates = couponRepository
                .findPublishCandidates(CouponCommand.Candidates.of(command.getCouponId(), start, end));

        List<UserCoupon> userCoupons = candidates.stream()
                .map(c -> UserCoupon.create(c.getUserId(), command.getCouponId(), c.getIssuedAt()))
                .toList();

        couponRepository.saveAllUserCoupons(userCoupons);
    }

    public boolean isPublishFinished(CouponCommand.PublishFinish command) {
        int publishedCount = couponRepository.countUserCouponsByCouponId(command.getCouponId());

        if (publishedCount > command.getQuantity()) {
            log.error("발급된 쿠폰 수가 발급 가능 수량을 초과했습니다. couponId: {}", command.getCouponId());
        }
        return publishedCount >= command.getQuantity();
    }
}