package com.github.gokid96.e_commerce.order.interfaces.event;

import com.github.gokid96.e_commerce.order.domain.OrderEvent;
import com.github.gokid96.e_commerce.order.domain.OrderEventPublisher;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.payment.domain.PaymentEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderPaymentEventListenerUnitTest {

    @InjectMocks
    private OrderPaymentEventListener eventListener;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @DisplayName("결제 완료 시 주문 완료 이벤트를 발행한다.")
    @Test
    void handlePaid() {
        // given
        PaymentEvent.Paid event = mock(PaymentEvent.Paid.class);

        // when
        eventListener.handle(event);

        // then
        verify(orderService, times(1)).completedOrder(any());
        verify(orderEventPublisher, times(1)).completed(any(OrderEvent.Completed.class));
    }

    @DisplayName("결제 완료 시, 주문 완료에 실패하면 주문 완료 실패 이벤트를 발행한다.")
    @Test
    void handlePaidFailed() {
        // given
        PaymentEvent.Paid event = mock(PaymentEvent.Paid.class);

        doThrow(new IllegalArgumentException("주문 완료 실패"))
                .when(orderService).completedOrder(any());

        // when
        eventListener.handle(event);

        // then
        verify(orderEventPublisher, times(1)).completeFailed(any(OrderEvent.CompleteFailed.class));
    }

    @DisplayName("결제 취소 시 주문 실패 이벤트를 발행한다.")
    @Test
    void handleCanceled() {
        // given
        PaymentEvent.Canceled event = mock(PaymentEvent.Canceled.class);

        // when
        eventListener.handle(event);

        // then
        verify(orderEventPublisher, times(1)).failed(any(OrderEvent.Failed.class));
    }

    @DisplayName("결제 실패 시 주문 실패 이벤트를 발행한다.")
    @Test
    void handlePayFailed() {
        // given
        PaymentEvent.PayFailed event = mock(PaymentEvent.PayFailed.class);

        // when
        eventListener.handle(event);

        // then
        verify(orderEventPublisher, times(1)).failed(any(OrderEvent.Failed.class));
    }
}