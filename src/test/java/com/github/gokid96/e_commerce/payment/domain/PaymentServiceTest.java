package com.github.gokid96.e_commerce.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
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
        PaymentCommand.Payment command = PaymentCommand.Payment
                .of(1L, 10_000L);

        // when
        paymentService.pay(command);

        // then
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }


}
