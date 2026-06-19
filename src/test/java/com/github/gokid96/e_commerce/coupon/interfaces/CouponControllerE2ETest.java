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

    @DisplayName("쿠폰 발급 시, 발급 가능한 상태여야 한다.")
    @Test
    void issueCouponWithInvalidStatus() {
        Coupon coupon = couponRepository.saveCoupon(
                Coupon.create("쿠폰명1", 0.1, 10, CouponStatus.REGISTERED, LocalDateTime.now().plusDays(1)));

        client.post()
                .uri("/api/v1/users/{userId}/coupons/issue", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("couponId", coupon.getId()))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo(500)
                .jsonPath("$.message").isEqualTo("발급 불가능한 쿠폰입니다.");
    }

    @DisplayName("쿠폰 발급 시, 만료된 쿠폰은 발급할 수 없다.")
    @Test
    void issueCouponWithExpiredDate() {
        Coupon coupon = couponRepository.saveCoupon(Coupon.builder()
                .name("쿠폰명1")
                .discountRate(0.1)
                .quantity(10)
                .status(CouponStatus.PUBLISHABLE)
                .expiredAt(LocalDateTime.now().minusDays(1))
                .build());

        client.post()
                .uri("/api/v1/users/{userId}/coupons/issue", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("couponId", coupon.getId()))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo(500)
                .jsonPath("$.message").isEqualTo("쿠폰이 만료되었습니다.");
    }

    @DisplayName("쿠폰 발급 시, 쿠폰 수량이 부족하면 발급할 수 없다.")
    @Test
    void issueCouponWithInsufficientQuantity() {
        Coupon coupon = couponRepository.saveCoupon(
                Coupon.create("쿠폰명1", 0.1, 0, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1)));

        client.post()
                .uri("/api/v1/users/{userId}/coupons/issue", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("couponId", coupon.getId()))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo(500)
                .jsonPath("$.message").isEqualTo("쿠폰이 모두 소진되었습니다.");
    }

    @DisplayName("쿠폰을 발급한다.")
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
