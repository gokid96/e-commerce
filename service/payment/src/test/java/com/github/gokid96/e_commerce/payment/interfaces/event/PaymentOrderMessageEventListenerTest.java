package com.github.gokid96.e_commerce.payment.interfaces.event;

import com.github.gokid96.e_commerce.payment.domain.PaymentCommand;
import com.github.gokid96.e_commerce.payment.domain.PaymentService;
import com.github.gokid96.e_commerce.payment.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.support.Acknowledgment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PaymentOrderMessageEventListenerTest extends MockTestSupport {

    @InjectMocks
    private PaymentOrderMessageEventListener paymentOrderMessageEventListener;

    @Mock
    private PaymentService paymentService;

    @Captor
    private ArgumentCaptor<PaymentCommand.Payment> commandCaptor;

    @DisplayName("주문 생성 이벤트를 수신하면 결제를 요청하고 오프셋을 커밋한다.")
    @Test
    void handleOrderCreated() {
        String message = """
                {
                    "eventId": "fee5d5ce-cdf7-4797-8baa-0cad19f80153",
                    "eventType": "ORDER_CREATED",
                    "payload": {
                        "orderId": 1,
                        "userId": 2,
                        "userCouponId": 3,
                        "totalPrice": 20000
                    }
                }
                """;
        Acknowledgment ack = mock(Acknowledgment.class);

        paymentOrderMessageEventListener.handleOrderCreated(message, ack);

        verify(paymentService).payPayment(commandCaptor.capture());
        PaymentCommand.Payment command = commandCaptor.getValue();
        assertThat(command.getOrderId()).isEqualTo(1L);
        assertThat(command.getUserId()).isEqualTo(2L);
        assertThat(command.getUserCouponId()).isEqualTo(3L);
        assertThat(command.getAmount()).isEqualTo(20_000L);
        verify(ack).acknowledge();
    }

    @DisplayName("주문 생성 이벤트에 쿠폰이 없으면 쿠폰 없이 결제를 요청한다.")
    @Test
    void handleOrderCreatedWithoutCoupon() {
        String message = """
                {
                    "eventId": "fee5d5ce-cdf7-4797-8baa-0cad19f80153",
                    "eventType": "ORDER_CREATED",
                    "payload": {
                        "orderId": 1,
                        "userId": 2,
                        "userCouponId": null,
                        "totalPrice": 20000
                    }
                }
                """;
        Acknowledgment ack = mock(Acknowledgment.class);

        paymentOrderMessageEventListener.handleOrderCreated(message, ack);

        verify(paymentService).payPayment(commandCaptor.capture());
        assertThat(commandCaptor.getValue().getUserCouponId()).isNull();
        verify(ack).acknowledge();
    }

    @DisplayName("주문 완료 실패 이벤트를 수신하면 결제를 취소하고 오프셋을 커밋한다.")
    @Test
    void handleOrderCompleteFailed() {
        String message = """
                {
                    "eventId": "fee5d5ce-cdf7-4797-8baa-0cad19f80153",
                    "eventType": "ORDER_COMPLETE_FAILED",
                    "payload": {
                        "orderId": 1
                    }
                }
                """;
        Acknowledgment ack = mock(Acknowledgment.class);

        paymentOrderMessageEventListener.handleOrderCompleteFailed(message, ack);

        verify(paymentService).cancelPayment(1L);
        verify(ack).acknowledge();
    }
}
