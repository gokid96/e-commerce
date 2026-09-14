package com.github.gokid96.e_commerce.payment.domain;

import com.github.gokid96.e_commerce.payment.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PaymentServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    @Mock
    private PaymentCompensationPublisher paymentCompensationPublisher;

    @DisplayName("결제 시 잔액을 차감하고 결제 완료 이벤트를 발행한다.")
    @Test
    void payPayment() {
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, null, 10_000L);

        paymentService.payPayment(command);

        verify(paymentClient, times(1)).useBalance(anyLong(), anyLong());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(paymentEventPublisher, times(1)).paid(any());
    }

    @DisplayName("쿠폰이 있으면 결제 시 쿠폰도 사용한다.")
    @Test
    void payPaymentWithCoupon() {
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, 5L, 10_000L);

        paymentService.payPayment(command);

        verify(paymentClient, times(1)).useCoupon(5L);
        verify(paymentEventPublisher, times(1)).paid(any());
    }

    @DisplayName("이미 결제된 주문이면 결제를 진행하지 않는다.")
    @Test
    void payPaymentWithAlreadyPaid() {
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, null, 10_000L);
        given(paymentRepository.findByOrderId(1L))
                .willReturn(Optional.of(Payment.create(1L, 10_000L)));

        paymentService.payPayment(command);

        verify(paymentClient, never()).useBalance(anyLong(), anyLong());
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentEventPublisher, never()).paid(any());
    }

    @DisplayName("결제 실패 시 보상 이벤트를 발행하고 예외를 전파한다.")
    @Test
    void payPaymentWithFailure() {
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, null, 10_000L);
        willThrow(new IllegalArgumentException("잔액이 부족합니다."))
                .given(paymentClient).useBalance(anyLong(), anyLong());

        assertThatThrownBy(() -> paymentService.payPayment(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔액이 부족합니다.");

        verify(paymentCompensationPublisher, times(1)).payFailed(1L);
        verify(paymentEventPublisher, never()).paid(any());
    }

    @DisplayName("결제를 취소하고 취소 이벤트를 발행한다.")
    @Test
    void cancelPayment() {
        Payment payment = Payment.create(1L, 10_000L);
        payment.pay();
        given(paymentRepository.findByOrderId(1L)).willReturn(Optional.of(payment));
        given(paymentClient.getOrder(1L)).willReturn(PaymentInfo.Order.of(1L, 1L, null, 10_000L));

        paymentService.cancelPayment(1L);

        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELED);
        verify(paymentClient, times(1)).refundBalance(anyLong(), anyLong());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(paymentEventPublisher, times(1)).canceled(any());
    }

    @DisplayName("결제 취소 시 결제가 존재하지 않으면 예외가 발생한다.")
    @Test
    void cancelPaymentWithoutPayment() {
        given(paymentRepository.findByOrderId(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.cancelPayment(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제가 존재하지 않습니다.");
    }
}
