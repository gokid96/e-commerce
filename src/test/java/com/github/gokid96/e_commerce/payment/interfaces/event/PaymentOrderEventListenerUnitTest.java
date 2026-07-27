package com.github.gokid96.e_commerce.payment.interfaces.event;

import com.github.gokid96.e_commerce.order.domain.OrderEvent;
import com.github.gokid96.e_commerce.payment.domain.PaymentEvent;
import com.github.gokid96.e_commerce.payment.domain.PaymentEventPublisher;
import com.github.gokid96.e_commerce.payment.domain.PaymentInfo;
import com.github.gokid96.e_commerce.payment.domain.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentOrderEventListenerUnitTest {

    @InjectMocks
    private PaymentOrderEventListener eventListener;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentEventPublisher eventPublisher;

    @DisplayName("주문 결제 대기 시, 결제 완료 이벤트를 발행한다.")
    @Test
    void handlePaymentWaited() {
        OrderEvent.PaymentWaited event = mock(OrderEvent.PaymentWaited.class);
        PaymentInfo.Payment payment = mock(PaymentInfo.Payment.class);

        when(paymentService.pay(any())).thenReturn(payment);
        when(payment.getPaymentId()).thenReturn(1L);

        eventListener.handle(event);

        verify(eventPublisher, times(1)).paid(any(PaymentEvent.Paid.class));
    }

    @DisplayName("주문 결제 대기 시, 결제 실패하면 실패 이벤트를 발행한다.")
    @Test
    void handlePaymentWaitedWithFailed() {
        OrderEvent.PaymentWaited event = mock(OrderEvent.PaymentWaited.class);

        when(paymentService.pay(any())).thenThrow(new RuntimeException("결제 실패"));

        eventListener.handle(event);

        verify(eventPublisher, times(1)).payFailed(any(PaymentEvent.PayFailed.class));
    }

    @DisplayName("주문 완료 실패 시, 결제를 취소한다.")
    @Test
    void handleCompleteFailed() {
        OrderEvent.CompleteFailed event = mock(OrderEvent.CompleteFailed.class);

        eventListener.handle(event);

        verify(paymentService, times(1)).cancelPayment(event.getPaymentId());
        verify(eventPublisher, times(1)).canceled(any(PaymentEvent.Canceled.class));
    }
}
