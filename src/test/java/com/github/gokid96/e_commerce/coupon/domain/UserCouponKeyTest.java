package com.github.gokid96.e_commerce.coupon.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserCouponKeyTest {

    @DisplayName("쿠폰 ID로 발급 요청 키를 생성한다.")
    @Test
    void of() {
        UserCouponKey key = UserCouponKey.of(5L);

        assertThat(key.generate()).isEqualTo("user_coupon:5");
    }
}