package com.github.gokid96.e_commerce.coupon.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserCouponTest {

    @DisplayName("사용자 쿠폰을 생성하면 초기 상태는 UNUSED 이다.")
    @Test
    void create() {
        // when
        UserCoupon userCoupon = UserCoupon.create(1L, 5L);

        // then
        assertThat(userCoupon.getUserId()).isEqualTo(1L);
        assertThat(userCoupon.getCouponId()).isEqualTo(5L);
        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.UNUSED);
    }

    @DisplayName("사용자 쿠폰을 사용하면 상태가 USED 로 변경된다.")
    @Test
    void use() {
        // given
        UserCoupon userCoupon = UserCoupon.create(1L, 5L);

        // when
        userCoupon.use();

        // then
        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.USED);
    }

    @DisplayName("이미 사용된 쿠폰은 다시 사용할 수 없다.")
    @Test
    void cannotUseAlreadyUsed(){
        // given
        UserCoupon userCoupon = UserCoupon.create(1L, 5L);
        userCoupon.use();

        // when & then
        assertThatThrownBy(userCoupon::use)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용된 쿠폰입니다.");
    }


}
