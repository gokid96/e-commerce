package com.github.gokid96.e_commerce.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    @InjectMocks
    private PaymentService paymentService;

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
}
