package com.github.gokid96.e_commerce.coupon.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    @DisplayName("쿠폰을 발급 종료 상태로 전환한다.")
    @Test
    void finish() {
        Coupon coupon = Coupon.create("선착순 쿠폰", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7));

        coupon.finish();

        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.FINISHED);
    }

    @DisplayName("쿠폰 생성 시 할인율은 0과 1 사이여야 한다.")
    @Test
    void createWithInvalidDiscountRate() {
        assertThatThrownBy(() -> Coupon.create("신규 가입 할인", 1.5, 100, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("할인율은 0과 1 사이여야 합니다.");
    }

    @DisplayName("쿠폰 생성 시 수량은 0 이상이어야 한다.")
    @Test
    void createWithNegativeQuantity() {
        assertThatThrownBy(() -> Coupon.create("신규 가입 할인", 0.1, -1, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 수량은 0 이상이어야 합니다.");
    }

    @DisplayName("쿠폰 생성 시 만료일은 현재 시간 이후여야 한다.")
    @Test
    void createWithPastExpiredAt() {
        assertThatThrownBy(() -> Coupon.create("신규 가입 할인", 0.1, 100, CouponStatus.PUBLISHABLE, LocalDateTime.now().minusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 만료일은 현재 시간 이후여야 합니다.");
    }

    @DisplayName("쿠폰을 발급하면 수량이 1 감소한다.")
    @Test
    void issue() {
        // given
        Coupon coupon = Coupon.create("신규 가입 할인", 0.1, 100, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7));

        // when
        coupon.issue();

        // then
        assertThat(coupon.getQuantity()).isEqualTo(99);
    }

    @DisplayName("발급 가능 상태가 아니면 발급할 수 없다.")
    @Test
    void cannotIssueWhenNotPublishable() {
        // given
        Coupon coupon = Coupon.create("신규 가입 할인", 0.1, 100, CouponStatus.REGISTERED, LocalDateTime.now().plusDays(7));

        // when & then
        assertThatThrownBy(coupon::issue)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("발급 불가능한 쿠폰입니다.");
    }

    @DisplayName("만료된 쿠폰은 발급할 수 없다.")
    @Test
    void cannotIssueWhenExpired() {
        // given — create()가 과거 만료일을 막으므로 builder로 직접 만료 상태를 만든다
        Coupon coupon = Coupon.builder()
                .name("신규 가입 할인")
                .discountRate(0.1)
                .quantity(100)
                .status(CouponStatus.PUBLISHABLE)
                .expiredAt(LocalDateTime.now().minusDays(1))
                .build();

        // when & then
        assertThatThrownBy(coupon::issue)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("쿠폰이 만료되었습니다.");
    }

    @DisplayName("수량이 모두 소진되면 발급할 수 없다.")
    @Test
    void cannotIssueWhenSoldOut() {
        // given
        Coupon coupon = Coupon.create("신규 가입 할인", 0.1, 1, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7));
        coupon.issue(); // 수량 1 → 0

        // when & then
        assertThatThrownBy(coupon::issue)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("쿠폰이 모두 소진되었습니다.");
    }
}