package com.github.gokid96.e_commerce.coupon.application;

import com.github.gokid96.e_commerce.coupon.domain.CouponCommand;
import com.github.gokid96.e_commerce.coupon.domain.CouponInfo;
import com.github.gokid96.e_commerce.coupon.domain.CouponService;
import com.github.gokid96.e_commerce.coupon.domain.UserCouponUsedStatus;
import com.github.gokid96.e_commerce.user.domain.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CouponFacadeTest {

    @Mock
    private UserService userService;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponFacade couponFacade;

    @DisplayName("쿠폰 발급 요청을 접수한다.")
    @Test
    void requestPublishUserCoupon() {
        couponFacade.requestPublishUserCoupon(CouponCriteria.PublishRequest.of(1L, 5L));

        verify(couponService, times(1)).requestPublishUserCoupon(any());
    }

    @DisplayName("발급 가능 쿠폰별로 후보를 발급한다.")
    @Test
    void publishUserCoupons() {
        given(couponService.getPublishableCoupons()).willReturn(CouponInfo.PublishableCoupons.of(List.of(
                CouponInfo.PublishableCoupon.of(5L, 10),
                CouponInfo.PublishableCoupon.of(6L, 20)
        )));

        couponFacade.publishUserCoupons(CouponCriteria.Publish.of(100));

        verify(couponService, times(2)).publishUserCoupons(any());
    }

    @DisplayName("수량이 소진된 쿠폰만 발급 종료 처리한다.")
    @Test
    void finishedPublishCoupons() {
        given(couponService.getPublishableCoupons()).willReturn(CouponInfo.PublishableCoupons.of(List.of(
                CouponInfo.PublishableCoupon.of(5L, 10),
                CouponInfo.PublishableCoupon.of(6L, 20)
        )));
        given(couponService.isPublishFinished(any())).willReturn(true, false);

        couponFacade.finishedPublishCoupons();

        verify(couponService, times(1)).finishCoupon(5L);
    }

    @DisplayName("쿠폰 사용 시 사용자 검증 후 사용이 순서대로 수행된다.")
    @Test
    void useCoupon() {
        // given
        CouponCriteria.Use criteria = CouponCriteria.Use.of(1L, 100L);

        // when
        couponFacade.useCoupon(criteria);

        // then
        InOrder inOrder = inOrder(userService, couponService);
        inOrder.verify(userService).getUser(1L);
        inOrder.verify(couponService).useCoupon(any(CouponCommand.Use.class));
    }

    @DisplayName("쿠폰 목록 조회 시 사용자 검증 후 조회가 순서대로 수행된다.")
    @Test
    void getUserCoupons() {
        // given
        CouponInfo.UserCoupon info = CouponInfo.UserCoupon.builder()
                .userCouponId(100L)
                .couponId(5L)
                .couponName("신규 가입 할인")
                .discountRate(0.1)
                .usedStatus(UserCouponUsedStatus.UNUSED)
                .build();
        given(couponService.getUserCoupons(1L)).willReturn(List.of(info));


        // when
        List<CouponResult.UserCoupon> result = couponFacade.getUserCoupons(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCouponName()).isEqualTo("신규 가입 할인");
        assertThat(result.get(0).getUsedStatus()).isEqualTo(UserCouponUsedStatus.UNUSED);

        InOrder inOrder = inOrder(userService, couponService);
        inOrder.verify(userService).getUser(1L);
        inOrder.verify(couponService).getUserCoupons(1L);
    }

}
