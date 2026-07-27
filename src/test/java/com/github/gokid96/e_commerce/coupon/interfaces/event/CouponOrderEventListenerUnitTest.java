package com.github.gokid96.e_commerce.coupon.interfaces.event;

import com.github.gokid96.e_commerce.coupon.domain.CouponEvent;
import com.github.gokid96.e_commerce.coupon.domain.CouponEventPublisher;
import com.github.gokid96.e_commerce.coupon.domain.CouponService;
import com.github.gokid96.e_commerce.order.domain.OrderEvent;
import com.github.gokid96.e_commerce.order.domain.OrderProcesses;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponOrderEventListenerUnitTest {

    @InjectMocks
    private CouponOrderEventListener orderEventListener;

    @Mock
    private CouponService couponService;

    @Mock
    private CouponEventPublisher eventPublisher;

    @DisplayName("주문 생성 시, 쿠폰 사용 완료 이벤트를 발행한다.")
    @Test
    void handleCreated() {
        // given
        OrderEvent.Created event = mock(OrderEvent.Created.class);
        when(event.getUserCouponId()).thenReturn(1L);

        // when
        orderEventListener.handle(event);

        // then
        verify(couponService, times(1)).useUserCoupon(anyLong());
        verify(eventPublisher, times(1)).used(any(CouponEvent.Used.class));
    }

    @DisplayName("주문 생성 시, 쿠폰 사용 실패하면 실패 이벤트를 발행한다.")
    @Test
    void handleCreatedWithFailed() {
        // given
        OrderEvent.Created event = mock(OrderEvent.Created.class);
        when(event.getUserCouponId()).thenReturn(1L);

        doThrow(new RuntimeException("쿠폰 사용 실패"))
                .when(couponService).useUserCoupon(anyLong());

        // when
        orderEventListener.handle(event);

        // then
        verify(eventPublisher, times(1)).useFailed(any(CouponEvent.UseFailed.class));
    }

    @DisplayName("주문 실패 시, 쿠폰 사용을 취소한다.")
    @Test
    void handleFailed() {
        // given
        OrderEvent.Failed event = mock(OrderEvent.Failed.class);
        OrderProcesses processes = mock(OrderProcesses.class);

        when(event.getProcesses()).thenReturn(processes);
        when(processes.isSuccess(any())).thenReturn(true);

        // when
        orderEventListener.handle(event);

        // then
        verify(couponService, times(1)).cancelUserCoupon(anyLong());
    }
}