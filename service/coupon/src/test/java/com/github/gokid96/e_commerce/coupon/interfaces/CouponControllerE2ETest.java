package com.github.gokid96.e_commerce.coupon.interfaces;

import com.github.gokid96.e_commerce.coupon.domain.Coupon;
import com.github.gokid96.e_commerce.coupon.domain.CouponClient;
import com.github.gokid96.e_commerce.coupon.domain.CouponInfo;
import com.github.gokid96.e_commerce.coupon.domain.CouponRepository;
import com.github.gokid96.e_commerce.coupon.domain.CouponStatus;
import com.github.gokid96.e_commerce.coupon.domain.UserCoupon;
import com.github.gokid96.e_commerce.coupon.support.E2EControllerTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Map;

class CouponControllerE2ETest extends E2EControllerTestSupport {

    @Autowired
    private CouponRepository couponRepository;

    @MockitoBean
    private CouponClient couponClient;

    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUpUser() {
        Mockito.when(couponClient.getUser(USER_ID))
                .thenReturn(CouponInfo.User.of(USER_ID, "유저"));
    }

    @DisplayName("보유한 쿠폰 목록을 가져온다.")
    @Test
    void getUserCoupons() {
        Coupon coupon1 = couponRepository.saveCoupon(
                Coupon.create("쿠폰명1", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1)));
        Coupon coupon2 = couponRepository.saveCoupon(
                Coupon.create("쿠폰명2", 0.2, 20, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1)));
        couponRepository.saveUserCoupon(UserCoupon.create(USER_ID, coupon1.getId()));
        couponRepository.saveUserCoupon(UserCoupon.create(USER_ID, coupon2.getId()));

        client.get()
                .uri("/api/v1/users/{userId}/coupons", USER_ID)
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
        couponRepository.updateAvailableCoupon(coupon.getId(), true);

        client.post()
                .uri("/api/v1/users/{userId}/coupons/issue", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("couponId", coupon.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.message").isEqualTo("OK");
    }
}
