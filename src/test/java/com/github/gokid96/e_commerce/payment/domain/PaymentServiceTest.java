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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @DisplayName("결제를 완료하고 저장한다.")
    @Test
    void pay() {
        // given
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 10_000L);

        // when
        paymentService.pay(command);

        // then
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @DisplayName("결제를 취소한다.")
    @Test
    void cancelPayment() {
        // given
        Payment payment = Payment.create(1L, 10_000L);
        payment.pay();
        given(paymentRepository.findById(1L)).willReturn(Optional.of(payment));

        // when
        paymentService.cancelPayment(1L);

        // then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELED);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}