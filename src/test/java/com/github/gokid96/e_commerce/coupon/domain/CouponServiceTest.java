package com.github.gokid96.e_commerce.coupon.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;



    @DisplayName("쿠폰을 사용한다.")
    @Test
    void useCoupon() {
        // given
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
    void useCoupon_notFound() {
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

        // when & then
        assertThatThrownBy(() -> couponService.useCoupon(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("본인의 쿠폰이 아닙니다.");
    }

    @DisplayName("주문용: 사용 가능한 쿠폰을 조회한다.")
    @Test
    void getUsableCoupon() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
                .id(100L).userId(1L).couponId(5L)
                .usedStatus(UserCouponUsedStatus.UNUSED)
                .issuedAt(LocalDateTime.now())
                .build();
        given(couponRepository.findUserCouponByUserIdAndCouponId(1L, 5L)).willReturn(userCoupon);

        CouponCommand.UsableCoupon command = CouponCommand.UsableCoupon.of(1L, 5L);

        // when
        CouponInfo.UsableCoupon result = couponService.getUsableCoupon(command);

        // then
        assertThat(result.getUserCouponId()).isEqualTo(100L);
    }

    @DisplayName("이미 사용된 쿠폰은 사용 가능 조회 시 예외가 발생한다.")
    @Test
    void getUsableCoupon_cannotUse() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
                .id(100L).userId(1L).couponId(5L)
                .usedStatus(UserCouponUsedStatus.USED)
                .issuedAt(LocalDateTime.now()).usedAt(LocalDateTime.now())
                .build();
        given(couponRepository.findUserCouponByUserIdAndCouponId(1L, 5L)).willReturn(userCoupon);

        CouponCommand.UsableCoupon command = CouponCommand.UsableCoupon.of(1L, 5L);

        // when & then
        assertThatThrownBy(() -> couponService.getUsableCoupon(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("사용할 수 없는 쿠폰입니다.");
    }

    @DisplayName("쿠폰 할인율을 조회한다.")
    @Test
    void getCoupon() {

        // given
        Coupon coupon = Coupon.create("10% 할인", 0.1, 100, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7));
        given(couponRepository.findCouponById(5L)).willReturn(Optional.of(coupon));

        // when
        CouponInfo.Coupon result = couponService.getCoupon(5L);

        // then
        assertThat(result.getDiscountRate()).isEqualTo(0.1);
        assertThat(result.getCouponName()).isEqualTo("10% 할인");
    }

    @DisplayName("발급되지 않은 쿠폰의 할인율은 조회할 수 없다.")
    @Test
    void getCoupon_notFound() {
        // given
        given(couponRepository.findCouponById(5L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> couponService.getCoupon(5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰이 존재하지 않습니다.");
    }

    @DisplayName("사용자가 보유한 사용 가능 쿠폰 목록을 조회한다.")
    @Test
    void getUserCoupons() {
        // given
        Coupon coupon = Coupon.create("신규 가입 할인", 0.1, 100, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7));
        UserCoupon userCoupon = UserCoupon.create(1L, 5L);

        given(couponRepository.findUserCouponsByUserIdAndUsedStatusIn(1L, UserCouponUsedStatus.forUsable()))
                .willReturn(List.of(userCoupon));
        given(couponRepository.findCouponById(5L))
                .willReturn(Optional.of(coupon));

        // when
        List<CouponInfo.UserCoupon> infos = couponService.getUserCoupons(1L);

        // then
        assertThat(infos).hasSize(1);
        assertThat(infos.get(0).getCouponName()).isEqualTo("신규 가입 할인");
        assertThat(infos.get(0).getDiscountRate()).isEqualTo(0.1);
        assertThat(infos.get(0).getUsedStatus()).isEqualTo(UserCouponUsedStatus.UNUSED);
    }

    @DisplayName("보유한 쿠폰이 없으면 빈 목록을 반환한다.")
    @Test
    void getUserCoupons_empty() {
        // given
        given(couponRepository.findUserCouponsByUserIdAndUsedStatusIn(1L, UserCouponUsedStatus.forUsable()))
                .willReturn(List.of());
        // when
        List<CouponInfo.UserCoupon> infos = couponService.getUserCoupons(1L);

        // then
        assertThat(infos).isEmpty();
    }

    @DisplayName("발급 가능 상태의 쿠폰 목록을 조회한다.")
    @Test
    void getPublishableCoupons() {
        // given
        Coupon coupon = Coupon.create("선착순 쿠폰", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7));
        given(couponRepository.findCouponsByStatus(CouponStatus.PUBLISHABLE)).willReturn(List.of(coupon));

        // when
        CouponInfo.PublishableCoupons result = couponService.getPublishableCoupons();

        // then
        assertThat(result.getCoupons()).hasSize(1)
                .extracting(CouponInfo.PublishableCoupon::getQuantity)
                .containsExactly(10);
    }

    @DisplayName("쿠폰 발급을 요청한다.")
    @Test
    void requestPublishUserCoupon() {
        given(couponRepository.savePublishRequest(any())).willReturn(true);

        boolean result = couponService.requestPublishUserCoupon(
                CouponCommand.PublishRequest.of(1L, 5L, LocalDateTime.now()));

        assertThat(result).isTrue();
    }

    @DisplayName("이미 요청한 사용자의 발급 요청은 실패한다.")
    @Test
    void requestPublishUserCoupon_duplicated() {
        given(couponRepository.savePublishRequest(any())).willReturn(false);

        assertThatThrownBy(() -> couponService.requestPublishUserCoupon(
                CouponCommand.PublishRequest.of(1L, 5L, LocalDateTime.now())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 발급 요청에 실패했습니다.");
    }

    @DisplayName("발급 후보를 꺼내 쿠폰을 발급한다.")
    @Test
    void publishUserCoupons() {
        given(couponRepository.countUserCouponsByCouponId(5L)).willReturn(0);
        given(couponRepository.findPublishCandidates(any())).willReturn(List.of(
                CouponInfo.Candidates.of(1L, LocalDateTime.now()),
                CouponInfo.Candidates.of(2L, LocalDateTime.now())
        ));

        couponService.publishUserCoupons(CouponCommand.Publish.of(5L, 10, 500));

        verify(couponRepository, times(1)).saveAllUserCoupons(any());
    }

    @DisplayName("수량이 모두 발급됐으면 발급하지 않는다.")
    @Test
    void publishUserCoupons_soldOut() {
        given(couponRepository.countUserCouponsByCouponId(5L)).willReturn(10);

        couponService.publishUserCoupons(CouponCommand.Publish.of(5L, 10, 500));

        verify(couponRepository, never()).saveAllUserCoupons(any());
    }

    @DisplayName("발급 수가 수량에 도달하면 발급 완료로 판정한다.")
    @Test
    void isPublishFinished() {
        given(couponRepository.countUserCouponsByCouponId(5L)).willReturn(10);

        boolean result = couponService.isPublishFinished(CouponCommand.PublishFinish.of(5L, 10));

        assertThat(result).isTrue();
    }

    @DisplayName("발급 수가 수량 미만이면 발급 미완료로 판정한다.")
    @Test
    void isNotPublishFinished() {
        given(couponRepository.countUserCouponsByCouponId(5L)).willReturn(9);

        boolean result = couponService.isPublishFinished(CouponCommand.PublishFinish.of(5L, 10));

        assertThat(result).isFalse();
    }

    @DisplayName("쿠폰을 발급 종료 처리한다.")
    @Test
    void finishCoupon() {
        // given
        Coupon coupon = Coupon.create("선착순 쿠폰", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7));
        given(couponRepository.findCouponById(5L)).willReturn(Optional.of(coupon));

        // when
        couponService.finishCoupon(5L);

        // then
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.FINISHED);
        verify(couponRepository, times(1)).saveCoupon(coupon);
    }

}
