package com.github.gokid96.e_commerce.coupon.domain;

import com.github.gokid96.e_commerce.support.ConcurrencyTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class CouponServiceConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;


    @DisplayName("동시에 선착순 발급해도 수량만큼 모두 정상 발급된다.")
    @Test
    void issueCouponWithPessimisticWriteLock() {
        // given
        Coupon coupon = Coupon.create("쿠폰명", 0.1, 5, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.saveCoupon(coupon);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        executeConcurrency(List.of(
                () -> issue(coupon.getId(), 1L, successCount, failCount),
                () -> issue(coupon.getId(), 2L, successCount, failCount)
        ));

        // then
        assertThat(successCount.get()).isEqualTo(2);
        assertThat(failCount.get()).isZero();

        Coupon remainCoupon = couponRepository.findCouponById(coupon.getId()).orElseThrow();
        assertThat(remainCoupon.getQuantity()).isEqualTo(3);
    }

    @DisplayName("동시에 선착순 발급 시 수량이 부족하면 예외가 발생한다.")
    @Test
    void issueCouponWhenInsufficientQuantity() {
        // given
        Coupon coupon = Coupon.create("쿠폰명", 0.1, 1, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.saveCoupon(coupon);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        executeConcurrency(List.of(
                () -> issue(coupon.getId(), 1L, successCount, failCount),
                () -> issue(coupon.getId(), 2L, successCount, failCount)
        ));

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        Coupon remainCoupon = couponRepository.findCouponById(coupon.getId()).orElseThrow();
        assertThat(remainCoupon.getQuantity()).isZero();

    }

    private void issue(Long couponId, Long userId, AtomicInteger successCount, AtomicInteger failCount) {
        try {
            couponService.issueCoupon(CouponCommand.Issue.of(userId, couponId));
            successCount.incrementAndGet();
        } catch (Exception e) {
            failCount.incrementAndGet();
        }
    }

}
