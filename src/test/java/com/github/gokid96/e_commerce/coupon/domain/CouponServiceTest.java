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

import static org.mockito.ArgumentMatchers.anyBoolean;
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

    @Mock
    private CouponEventPublisher couponEventPublisher;

    @InjectMocks
    private CouponService couponService;

    // ===== 사용/조회 (기존 유지) =====

    @DisplayName("쿠폰을 사용한다.")
    @Test
    void useCoupon() {
        UserCoupon userCoupon = UserCoupon.create(1L, 5L);
        given(couponRepository.findUserCouponById(100L)).willReturn(Optional.of(userCoupon));

        couponService.useCoupon(CouponCommand.Use.of(1L, 100L));

        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.USED);
        verify(couponRepository, times(1)).saveUserCoupon(userCoupon);
    }

    @DisplayName("사용자 쿠폰 사용을 취소한다.")
    @Test
    void cancelUserCoupon() {
        UserCoupon userCoupon = UserCoupon.create(1L, 5L);
        userCoupon.use();
        given(couponRepository.findUserCouponById(100L)).willReturn(Optional.of(userCoupon));

        couponService.cancelUserCoupon(100L);

        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.UNUSED);
        verify(couponRepository, times(1)).saveUserCoupon(any());
    }

    @DisplayName("존재하지 않는 사용자 쿠폰은 사용할 수 없다.")
    @Test
    void useCoupon_notFound() {
        given(couponRepository.findUserCouponById(100L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.useCoupon(CouponCommand.Use.of(1L, 100L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("발급된 쿠폰이 존재하지 않습니다.");
    }

    @DisplayName("본인의 쿠폰이 아니면 사용할 수 없다.")
    @Test
    void useCoupon_notOwner() {
        UserCoupon userCoupon = UserCoupon.create(2L, 5L);
        given(couponRepository.findUserCouponById(100L)).willReturn(Optional.of(userCoupon));

        assertThatThrownBy(() -> couponService.useCoupon(CouponCommand.Use.of(1L, 100L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("본인의 쿠폰이 아닙니다.");
    }

    @DisplayName("주문용: 사용 가능한 쿠폰을 조회한다.")
    @Test
    void getUsableCoupon() {
        UserCoupon userCoupon = UserCoupon.builder()
                .id(100L).userId(1L).couponId(5L)
                .usedStatus(UserCouponUsedStatus.UNUSED)
                .issuedAt(LocalDateTime.now())
                .build();
        given(couponRepository.findUserCouponByUserIdAndCouponId(1L, 5L)).willReturn(userCoupon);

        CouponInfo.UsableCoupon result = couponService.getUsableCoupon(CouponCommand.UsableCoupon.of(1L, 5L));

        assertThat(result.getUserCouponId()).isEqualTo(100L);
    }

    @DisplayName("이미 사용된 쿠폰은 사용 가능 조회 시 예외가 발생한다.")
    @Test
    void getUsableCoupon_cannotUse() {
        UserCoupon userCoupon = UserCoupon.builder()
                .id(100L).userId(1L).couponId(5L)
                .usedStatus(UserCouponUsedStatus.USED)
                .issuedAt(LocalDateTime.now()).usedAt(LocalDateTime.now())
                .build();
        given(couponRepository.findUserCouponByUserIdAndCouponId(1L, 5L)).willReturn(userCoupon);

        assertThatThrownBy(() -> couponService.getUsableCoupon(CouponCommand.UsableCoupon.of(1L, 5L)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("사용할 수 없는 쿠폰입니다.");
    }

    @DisplayName("쿠폰 할인율을 조회한다.")
    @Test
    void getCoupon() {
        Coupon coupon = Coupon.create("10% 할인", 0.1, 100, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7));
        given(couponRepository.findCouponById(5L)).willReturn(Optional.of(coupon));

        CouponInfo.Coupon result = couponService.getCoupon(5L);

        assertThat(result.getDiscountRate()).isEqualTo(0.1);
        assertThat(result.getCouponName()).isEqualTo("10% 할인");
    }

    @DisplayName("발급되지 않은 쿠폰의 할인율은 조회할 수 없다.")
    @Test
    void getCoupon_notFound() {
        given(couponRepository.findCouponById(5L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.getCoupon(5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰이 존재하지 않습니다.");
    }

    @DisplayName("사용자가 보유한 사용 가능 쿠폰 목록을 조회한다.")
    @Test
    void getUserCoupons() {
        Coupon coupon = Coupon.create("신규 가입 할인", 0.1, 100, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7));
        UserCoupon userCoupon = UserCoupon.create(1L, 5L);
        given(couponRepository.findUserCouponsByUserIdAndUsedStatusIn(1L, UserCouponUsedStatus.forUsable()))
                .willReturn(List.of(userCoupon));
        given(couponRepository.findCouponById(5L)).willReturn(Optional.of(coupon));

        List<CouponInfo.UserCoupon> infos = couponService.getUserCoupons(1L);

        assertThat(infos).hasSize(1);
        assertThat(infos.get(0).getCouponName()).isEqualTo("신규 가입 할인");
    }

    @DisplayName("보유한 쿠폰이 없으면 빈 목록을 반환한다.")
    @Test
    void getUserCoupons_empty() {
        given(couponRepository.findUserCouponsByUserIdAndUsedStatusIn(1L, UserCouponUsedStatus.forUsable()))
                .willReturn(List.of());

        assertThat(couponService.getUserCoupons(1L)).isEmpty();
    }

    // ===== 발급 (Kafka 방식으로 교체) =====

    @DisplayName("발급 가능한 쿠폰이면 발급 요청 이벤트를 발행한다.")
    @Test
    void requestPublishUserCoupon() {
        given(couponRepository.findPublishableCouponById(5L)).willReturn(true);

        couponService.requestPublishUserCoupon(CouponCommand.Publish.of(1L, 5L));

        verify(couponEventPublisher, times(1)).publishRequested(any());
    }

    @DisplayName("발급 불가한 쿠폰이면 요청이 실패한다.")
    @Test
    void requestPublishUserCoupon_notPublishable() {
        given(couponRepository.findPublishableCouponById(5L)).willReturn(false);

        assertThatThrownBy(() -> couponService.requestPublishUserCoupon(CouponCommand.Publish.of(1L, 5L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("발급 불가한 쿠폰입니다.");
        verify(couponEventPublisher, never()).publishRequested(any());
    }

    @DisplayName("쿠폰을 발급하면 수량이 1 차감되고 발행 완료 이벤트를 발행한다.")
    @Test
    void publishUserCoupon() {
        Coupon coupon = Coupon.create("선착순 쿠폰", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7));
        given(couponRepository.findOptionalUserCouponByUserIdAndCouponId(1L, 5L)).willReturn(Optional.empty());
        given(couponRepository.findCouponById(5L)).willReturn(Optional.of(coupon));

        couponService.publishUserCoupon(CouponCommand.Publish.of(1L, 5L));

        assertThat(coupon.getQuantity()).isEqualTo(9);
        verify(couponRepository, times(1)).saveUserCoupon(any());
        verify(couponEventPublisher, times(1)).published(any());
    }

    @DisplayName("이미 발급받은 사용자는 다시 발급되지 않는다.")
    @Test
    void publishUserCoupon_duplicated() {
        given(couponRepository.findOptionalUserCouponByUserIdAndCouponId(1L, 5L))
                .willReturn(Optional.of(UserCoupon.create(1L, 5L)));

        assertThatThrownBy(() -> couponService.publishUserCoupon(CouponCommand.Publish.of(1L, 5L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 발급된 쿠폰입니다.");
        verify(couponRepository, never()).saveUserCoupon(any());
    }

    @DisplayName("수량이 소진되면 발급 가능 상태를 내린다.")
    @Test
    void stopPublishCoupon_soldOut() {
        Coupon coupon = Coupon.create("선착순 쿠폰", 0.1, 0, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7));
        given(couponRepository.findCouponById(5L)).willReturn(Optional.of(coupon));

        couponService.stopPublishCoupon(5L);

        verify(couponRepository, times(1)).updateAvailableCoupon(5L, false);
    }

    @DisplayName("수량이 남아있으면 발급 가능 상태를 유지한다.")
    @Test
    void stopPublishCoupon_available() {
        Coupon coupon = Coupon.create("선착순 쿠폰", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7));
        given(couponRepository.findCouponById(5L)).willReturn(Optional.of(coupon));

        couponService.stopPublishCoupon(5L);

        verify(couponRepository, never()).updateAvailableCoupon(any(), anyBoolean());
    }
}