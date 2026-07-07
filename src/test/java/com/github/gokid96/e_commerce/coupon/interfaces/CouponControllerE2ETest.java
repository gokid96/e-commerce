package com.github.gokid96.e_commerce.coupon.interfaces;

import com.github.gokid96.e_commerce.coupon.domain.Coupon;
import com.github.gokid96.e_commerce.coupon.domain.CouponRepository;
import com.github.gokid96.e_commerce.coupon.domain.CouponStatus;
import com.github.gokid96.e_commerce.coupon.domain.UserCoupon;
import com.github.gokid96.e_commerce.support.E2EControllerTestSupport;
import com.github.gokid96.e_commerce.user.domain.User;
import com.github.gokid96.e_commerce.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.Map;

class CouponControllerE2ETest extends E2EControllerTestSupport {

    @Autowired private UserRepository userRepository;
    @Autowired private CouponRepository couponRepository;

    private User user;

    @BeforeEach
    void setUpUser() {
        user = userRepository.save(User.create("유저"));
    }

    @DisplayName("보유한 쿠폰 목록을 가져온다.")
    @Test
    void getUserCoupons() {
        Coupon coupon1 = couponRepository.saveCoupon(
                Coupon.create("쿠폰명1", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1)));
        Coupon coupon2 = couponRepository.saveCoupon(
                Coupon.create("쿠폰명2", 0.2, 20, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1)));
        couponRepository.saveUserCoupon(UserCoupon.create(user.getId(), coupon1.getId()));
        couponRepository.saveUserCoupon(UserCoupon.create(user.getId(), coupon2.getId()));

        client.get()
                .uri("/api/v1/users/{userId}/coupons", user.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data[0].couponName").isEqualTo("쿠폰명1")
                .jsonPath("$.data[1].couponName").isEqualTo("쿠폰명2");
    }

    @DisplayName("쿠폰 발급을 요청하면 접수된다.")
    @Test
    void issueCoupon() {
        Coupon coupon = couponRepository.saveCoupon(
                Coupon.create("쿠폰명1", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1)));

        client.post()
                .uri("/api/v1/users/{userId}/coupons/issue", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("couponId", coupon.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.message").isEqualTo("OK");
    }
}
