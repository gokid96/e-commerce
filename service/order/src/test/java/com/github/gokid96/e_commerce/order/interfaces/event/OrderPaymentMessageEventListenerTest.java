package com.github.gokid96.e_commerce.order.interfaces.event;

import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.order.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.support.Acknowledgment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class OrderPaymentMessageEventListenerTest extends MockTestSupport {

    @InjectMocks
    private OrderPaymentMessageEventListener orderPaymentMessageEventListener;

    @Mock
    private OrderService orderService;

    @DisplayName("결제 완료 이벤트를 수신하면 주문을 완료하고 오프셋을 커밋한다.")
    @Test
    void handlePaymentPaid() {
        String message = """
                {
                    "eventId": "9b340cc0-6fc6-4f0e-b334-fa183ed4ff3a",
                    "eventType": "PAYMENT_PAID",
                    "payload": {
                        "paymentId": 1,
                        "orderId": 10,
                        "userId": 2,
                        "totalPrice": 20000
                    }
                }
                """;
        Acknowledgment ack = mock(Acknowledgment.class);

        orderPaymentMessageEventListener.handlePaymentPaid(message, ack);

        verify(orderService).completedOrder(10L);
        verify(ack).acknowledge();
    }

    @DisplayName("결제 실패 이벤트를 수신하면 주문을 취소하고 오프셋을 커밋한다.")
    @Test
    void handlePaymentFailed() {
        String message = """
                {
                    "eventId": "9b340cc0-6fc6-4f0e-b334-fa183ed4ff3a",
                    "eventType": "PAYMENT_FAILED",
                    "payload": {
                        "orderId": 10
                    }
                }
                """;
        Acknowledgment ack = mock(Acknowledgment.class);

        orderPaymentMessageEventListener.handlePaymentFailed(message, ack);

        verify(orderService).cancelOrder(10L);
        verify(ack).acknowledge();
    }

    @DisplayName("결제 취소 이벤트를 수신하면 주문을 취소하고 오프셋을 커밋한다.")
    @Test
    void handlePaymentCanceled() {
        String message = """
                {
                    "eventId": "9b340cc0-6fc6-4f0e-b334-fa183ed4ff3a",
                    "eventType": "PAYMENT_CANCELED",
                    "payload": {
                        "orderId": 10
                    }
                }
                """;
        Acknowledgment ack = mock(Acknowledgment.class);

        orderPaymentMessageEventListener.handlePaymentCanceled(message, ack);

        verify(orderService).cancelOrder(10L);
        verify(ack).acknowledge();
    }

    @DisplayName("주문 처리에 실패하면 오프셋을 커밋하지 않고 예외를 전파한다.")
    @Test
    void handlePaymentPaidWhenFailed() {
        String message = """
                {
                    "eventId": "9b340cc0-6fc6-4f0e-b334-fa183ed4ff3a",
                    "eventType": "PAYMENT_PAID",
                    "payload": {
                        "paymentId": 1,
                        "orderId": 10,
                        "userId": 2,
                        "totalPrice": 20000
                    }
                }
                """;
        Acknowledgment ack = mock(Acknowledgment.class);
        willThrow(new IllegalArgumentException("주문이 존재하지 않습니다."))
                .given(orderService).completedOrder(10L);

        // 커밋 전에 예외가 전파되어야 재전달로 복구할 수 있다.
        assertThatThrownBy(() -> orderPaymentMessageEventListener.handlePaymentPaid(message, ack))
                .isInstanceOf(IllegalArgumentException.class);

        verify(ack, never()).acknowledge();
    }
}
