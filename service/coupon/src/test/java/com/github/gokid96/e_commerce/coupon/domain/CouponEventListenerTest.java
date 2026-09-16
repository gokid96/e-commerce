package com.github.gokid96.e_commerce.coupon.domain;

import com.github.gokid96.e_commerce.coupon.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CouponEventListenerTest extends MockTestSupport {

    @InjectMocks
    private CouponEventListener couponEventListener;

    @Mock
    private CouponService couponService;

    @DisplayName("쿠폰 발급 이벤트를 수신하면 발급 중단 여부를 확인한다.")
    @Test
    void handle() {
        CouponEvent.Published event = new CouponEvent.Published(5L);

        couponEventListener.handle(event);

        verify(couponService, times(1)).stopPublishCoupon(5L);
    }
}
