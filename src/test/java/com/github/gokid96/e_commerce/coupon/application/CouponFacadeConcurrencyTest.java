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

import static org.assertj.core.api.Assertions.assertThat;

public class CouponFacadeConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired
    private CouponFacade couponFacade;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CouponRepository couponRepository;


    @DisplayName("동시성 - 한 사용자가 동일 쿠폰을 여러 개 발급받을 수 없다.")
    @Test
    void issueCouponConcurrencySameUser() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        Coupon coupon = Coupon.create("쿠폰명", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.saveCoupon(coupon);

        CouponCriteria.Issue criteria = CouponCriteria.Issue.of(user.getId(), coupon.getId());

        // when
        executeConcurrency(3, () -> couponFacade.issueCoupon(criteria));

        // then (목표: 한 사용자는 1개만 발급 → 수량 9)
        assertThat(couponRepository.findUserCouponsByUserId(user.getId())).hasSize(1);

        Coupon remainCoupon = couponRepository.findCouponById(coupon.getId()).orElseThrow();
        assertThat(remainCoupon.getQuantity()).isEqualTo(9);
    }


    @DisplayName("동시성 - 쿠폰 발급 수량을 초과해서 발급할 수 없다.")
    @Test
    void issueCouponConcurrency() {
        // given
        User user1 = User.create("유저1");
        userRepository.save(user1);
        User user2 = User.create("유저2");
        userRepository.save(user1);
        User user3 = User.create("유저3");
        userRepository.save(user1);

        Coupon coupon = Coupon.create("쿠폰명", 0.1, 2, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.saveCoupon(coupon);

        CouponCriteria.Issue criteria1 = CouponCriteria.Issue.of(user1.getId(), coupon.getId());
        CouponCriteria.Issue criteria2 = CouponCriteria.Issue.of(user2.getId(), coupon.getId());
        CouponCriteria.Issue criteria3 = CouponCriteria.Issue.of(user3.getId(), coupon.getId());

        // when
        executeConcurrency(List.of(
                () -> couponFacade.issueCoupon(criteria1),
                () -> couponFacade.issueCoupon(criteria3),
                () -> couponFacade.issueCoupon(criteria3)
        ));
        // then
        Coupon remainCoupon = couponRepository.findCouponById(coupon.getId()).orElseThrow();
        assertThat(remainCoupon.getQuantity()).isZero();
    }

}
