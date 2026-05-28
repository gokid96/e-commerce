package com.github.gokid96.e_commerce.coupon.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    @DisplayName("쿠폰 생성 시 할인 금액은 0보다 커야 한다.")
    @Test
    void createWithNotPositiveDiscountAmount() {
        assertThatThrownBy(() -> Coupon.create("신규 가입 할인", 0L, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("할인 금액은 양수여야 합니다.");
    }

    @DisplayName("쿠폰 생성 시 발급 수량은 0보다 커야 한다.")
    @Test
    void createWithNotPositiveTotalQuantity() {
        assertThatThrownBy(() -> Coupon.create("신규 가입 할인", 10_000L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("발급 수량은 양수여야 합니다.");
    }

    @DisplayName("쿠폰을 발급하면 발급 수량이 증가한다.")
    @Test
    void issue() {
        // given
        Coupon coupon = Coupon.create("신규 가입 할인", 10_000L, 100);

        // when
        coupon.issue();

        // then
        assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.AVAILABLE);
    }

    @DisplayName("쿠폰 발급으로 수량이 모두 소진되면 상태가 UNAVAILABLE 로 변경된다.")
    @Test
    void issueLastCoupon() {
        // given
        Coupon coupon = Coupon.create("신규 가입 할인", 10_000L, 1);

        // when
        coupon.issue();

        // then
        assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.UNAVAILABLE);
    }

    @DisplayName("쿠폰이 모두 소진된 경우 발급할 수 없다.")
    @Test
    void cannotIssueWhenSoldOut() {
        // given
        Coupon coupon = Coupon.create("신규 가입 할인", 10_000L, 1);
        coupon.issue();

        // when & then
        assertThatThrownBy(coupon::issue)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("발급 불가능한 쿠폰입니다.");
    }
}