package com.github.gokid96.e_commerce.coupon.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @DisplayName("쿠폰을 발급한다.")
    @Test
    void issueCoupon() {
        // given
        Coupon coupon = Coupon.create("신규 가입 할인", 10_000L, 100);
        given(couponRepository.findCouponById(5L)).willReturn(Optional.of(coupon));

        CouponCommand.Issue command = CouponCommand.Issue.of(1L, 5L);

        // when
        couponService.issueCoupon(command);

        assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
        verify(couponRepository, times(1)).saveCoupon(coupon);
        verify(couponRepository, times(1)).saveUserCoupon(any(UserCoupon.class));
    }

    @DisplayName("존재하지 않는 쿠폰은 발급할 수 없다.")
    @Test
    void issueCoupon_notFound() {
        // given
        given(couponRepository.findCouponById(5L)).willReturn(Optional.empty());

        CouponCommand.Issue command = CouponCommand.Issue.of(1L, 5L);

        // when & then
        assertThatThrownBy(() -> couponService.issueCoupon(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰이 존재하지 않습니다.");
    }

    @DisplayName("쿠폰을 사용한다.")
    @Test
    void useCoupon() {
        //given
        UserCoupon userCoupon = UserCoupon.create(1L, 5L);
        given(couponRepository.findUserCouponById(100L)).willReturn(Optional.of(userCoupon));

        CouponCommand.Use command = CouponCommand.Use.of(1L, 100L);

        // when
        couponService.useCoupon(command);

        // then
        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.USED);
        verify(couponRepository, times(1)).saveUserCoupon(userCoupon);
    }

    @DisplayName("존재하지 않는 사용자 쿠폰은 사용할 수 없다.")
    @Test
    void useCoupon_netFound() {
        // given
        given(couponRepository.findUserCouponById(100L)).willReturn(Optional.empty());

        CouponCommand.Use command = CouponCommand.Use.of(1L, 100L);
        // when & then
        assertThatThrownBy(() -> couponService.useCoupon(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("발급된 쿠폰이 존재하지 않습니다.");
    }

    @DisplayName("본인의 쿠폰이 아니면 사용할 수 없다.")
    @Test
    void useCoupon_notOwner() {
        // given
        UserCoupon userCoupon = UserCoupon.create(2L, 5L); // 사용자 2번
        given(couponRepository.findUserCouponById(100L)).willReturn(Optional.of(userCoupon));

        CouponCommand.Use command = CouponCommand.Use.of(1L, 100L); // 사용자 1번

        assertThatThrownBy(() -> couponService.useCoupon(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("본인의 쿠폰이 아닙니다.");
    }

    @DisplayName("사용자가 보유한 쿠폰 목록을 조회한다.")
    @Test
    void getUserCoupons(){
        // given
        Coupon coupon = Coupon.create("신규 가입 할인",10_000L,100);
        UserCoupon userCoupon = UserCoupon.create(1L, 5L);

        given(couponRepository.findUserCouponsByUserId(1L))
                .willReturn(List.of(userCoupon));
        given(couponRepository.findCouponById(5L))
                .willReturn(Optional.of(coupon));

        // when
        List<CouponInfo.UserCoupon> infos = couponService.getUserCoupons(1L);

        // then
        assertThat(infos).hasSize(1);
        assertThat(infos.get(0).getCouponName()).isEqualTo("신규 가입 할인");
        assertThat(infos.get(0).getDiscountAmount()).isEqualTo(10_000L);
        assertThat(infos.get(0).getUsedStatus()).isEqualTo(UserCouponUsedStatus.UNUSED);
    }

    @DisplayName("보유한 쿠폰이 없으면 빈 목록을 반환한다.")
    @Test
    void getUserCoupons_empty(){
        // given
        given(couponRepository.findUserCouponsByUserId(1L)).willReturn(List.of());

        // when
        List<CouponInfo.UserCoupon> infos = couponService.getUserCoupons(1L);

        // then
        assertThat(infos).isEmpty();
    }


}
