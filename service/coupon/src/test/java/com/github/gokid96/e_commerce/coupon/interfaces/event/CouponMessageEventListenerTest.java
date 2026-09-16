package com.github.gokid96.e_commerce.coupon.interfaces.event;

import com.github.gokid96.e_commerce.coupon.domain.CouponCommand;
import com.github.gokid96.e_commerce.coupon.domain.CouponService;
import com.github.gokid96.e_commerce.coupon.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.support.Acknowledgment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class CouponMessageEventListenerTest extends MockTestSupport {

    @InjectMocks
    private CouponMessageEventListener couponMessageEventListener;

    @Mock
    private CouponService couponService;

    @Captor
    private ArgumentCaptor<CouponCommand.Publish> commandCaptor;

    private static final String MESSAGE = """
            {
                "eventId": "2c9f8d3e-2b6a-4a1e-9c1d-8f0a5f3b7c11",
                "eventType": "COUPON_PUBLISH_REQUESTED",
                "payload": {
                    "userId": 1,
                    "couponId": 5
                }
            }
            """;

    @DisplayName("쿠폰 발급 요청 이벤트를 수신하면 쿠폰을 발급하고 오프셋을 커밋한다.")
    @Test
    void handle() {
        Acknowledgment ack = mock(Acknowledgment.class);

        couponMessageEventListener.handle(MESSAGE, ack);

        verify(couponService).publishUserCoupon(commandCaptor.capture());
        CouponCommand.Publish command = commandCaptor.getValue();
        assertThat(command.getUserId()).isEqualTo(1L);
        assertThat(command.getCouponId()).isEqualTo(5L);
        verify(ack).acknowledge();
    }

    @DisplayName("이미 발급된 쿠폰이면 오프셋을 커밋하지 않고 예외를 전파한다.")
    @Test
    void handleWhenAlreadyPublished() {
        Acknowledgment ack = mock(Acknowledgment.class);
        willThrow(new IllegalArgumentException("이미 발급된 쿠폰입니다."))
                .given(couponService).publishUserCoupon(any(CouponCommand.Publish.class));

        assertThatThrownBy(() -> couponMessageEventListener.handle(MESSAGE, ack))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 발급된 쿠폰입니다.");

        verify(ack, never()).acknowledge();
    }
}
