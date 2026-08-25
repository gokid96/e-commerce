package com.github.gokid96.e_commerce.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PaymentTest {

    @DisplayName("결제 금액은 0보다 커야 한다.")
    @Test
    void createWithNotPositiveAmount(){
        assertThatThrownBy(()-> Payment.create(1L,0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 금액은 0보다 커야 합니다.");
    }

    @DisplayName("결제를 완료한다.")
    @Test
    void pay(){
        // given
        Payment payment = Payment.create(1L,10_000L);

        // when
        payment.pay();

        // then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @DisplayName("결제를 취소하면 상태가 CANCELED 이다.")
    @Test
    void cancel() {
        // given
        Payment payment = Payment.create(1L, 10_000L);
        payment.pay();

        // when
        payment.cancel();

        // then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELED);
    }

    @DisplayName("완료 상태는 결제 불가, 준비 상태는 결제 가능이다.")
    @Test
    void cannotPayable(){
        assertThat(PaymentStatus.COMPLETED.cannotPayable()).isTrue();
        assertThat(PaymentStatus.READY.cannotPayable()).isFalse();
    }

}
