package com.github.gokid96.e_commerce.coupon.application;

import com.github.gokid96.e_commerce.coupon.domain.Coupon;
import com.github.gokid96.e_commerce.coupon.domain.CouponRepository;
import com.github.gokid96.e_commerce.coupon.domain.CouponStatus;
import com.github.gokid96.e_commerce.support.ConcurrencyTestSupport;
import com.github.gokid96.e_commerce.user.domain.User;
import com.github.gokid96.e_commerce.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class CouponFacadeLockTest extends ConcurrencyTestSupport {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("분산락 - 동시에 선착순 발급 시, 모든 요청에 대해 발급되어야 한다.")
    @Test
    void issueCouponWithDistributedLock() {
        // given
        User user1 = userRepository.save(User.create("항플러1"));
        User user2 = userRepository.save(User.create("항플러2"));

        Coupon coupon = Coupon.create("쿠폰명", 0.1, 5, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.saveCoupon(coupon);

        CouponCriteria.Issue criteria1 = CouponCriteria.Issue.of(user1.getId(), coupon.getId());
        CouponCriteria.Issue criteria2 = CouponCriteria.Issue.of(user2.getId(), coupon.getId());

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(List.of(
                () -> {
                    try {
                        couponFacade.issueCoupon(criteria1);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    }
                },
                () -> {
                    try {
                        couponFacade.issueCoupon(criteria2);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    }
                }
        ));

        // then
        assertThat(successCount.get()).isEqualTo(2);
        assertThat(failCount.get()).isZero();

        Coupon remainCoupon = couponRepository.findCouponById(coupon.getId()).orElseThrow();
        assertThat(remainCoupon.getQuantity()).isEqualTo(3);
    }
}
