package com.github.gokid96.e_commerce.coupon.application;

import com.github.gokid96.e_commerce.coupon.domain.CouponClient;
import com.github.gokid96.e_commerce.coupon.domain.CouponCommand;
import com.github.gokid96.e_commerce.coupon.domain.CouponInfo;
import com.github.gokid96.e_commerce.coupon.domain.CouponService;
import com.github.gokid96.e_commerce.coupon.domain.UserCouponUsedStatus;
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
    private CouponClient couponClient;

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

    @DisplayName("쿠폰 사용 시 사용자 검증 후 사용이 순서대로 수행된다.")
    @Test
    void useCoupon() {
        couponFacade.useCoupon(CouponCriteria.Use.of(1L, 100L));

        InOrder inOrder = inOrder(couponClient, couponService);
        inOrder.verify(couponClient).getUser(1L);
        inOrder.verify(couponService).useCoupon(any(CouponCommand.Use.class));
    }

    @DisplayName("쿠폰 목록 조회 시 사용자 검증 후 조회가 순서대로 수행된다.")
    @Test
    void getUserCoupons() {
        CouponInfo.UserCoupon info = CouponInfo.UserCoupon.builder()
                .userCouponId(100L)
                .couponId(5L)
                .couponName("신규 가입 할인")
                .discountRate(0.1)
                .usedStatus(UserCouponUsedStatus.UNUSED)
                .build();
        given(couponService.getUserCoupons(1L)).willReturn(List.of(info));

        List<CouponResult.UserCoupon> result = couponFacade.getUserCoupons(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCouponName()).isEqualTo("신규 가입 할인");

        InOrder inOrder = inOrder(couponClient, couponService);
        inOrder.verify(couponClient).getUser(1L);
        inOrder.verify(couponService).getUserCoupons(1L);
    }
}
