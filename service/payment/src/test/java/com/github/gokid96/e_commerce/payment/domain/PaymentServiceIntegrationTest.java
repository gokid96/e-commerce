package com.github.gokid96.e_commerce.payment.domain;

import com.github.gokid96.e_commerce.outbox.domain.OutboxEvent;
import com.github.gokid96.e_commerce.payment.support.IntegrationTestSupport;
import com.github.gokid96.e_commerce.payment.support.database.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PaymentServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @MockitoBean
    private PaymentClient paymentClient;

    @MockitoSpyBean
    private PaymentEventPublisher paymentEventPublisher;

    @AfterEach
    void tearDown() {
        databaseCleaner.clean();
    }

    @DisplayName("결제에 성공하면 결제가 저장되고 결제 완료 이벤트가 발행된다.")
    @Test
    void payPayment() {
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, 1L, 10_000L);

        paymentService.payPayment(command);

        Payment payment = paymentRepository.findByOrderId(1L).orElseThrow();
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getAmount()).isEqualTo(10_000L);
        verify(paymentClient).useBalance(1L, 10_000L);
        verify(paymentClient).useCoupon(1L);
        verify(paymentEventPublisher).paid(any(PaymentEvent.Paid.class));
        assertThat(events.stream(OutboxEvent.class).count()).isEqualTo(1);
    }

    @DisplayName("결제 시 쿠폰이 없으면 쿠폰을 사용하지 않는다.")
    @Test
    void payPaymentWithoutCoupon() {
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, null, 10_000L);

        paymentService.payPayment(command);

        verify(paymentClient, never()).useCoupon(anyLong());
        verify(paymentEventPublisher).paid(any(PaymentEvent.Paid.class));
    }

    @DisplayName("잔액 사용에 실패하면 결제는 롤백되지만 보상 이벤트는 남는다.")
    @Test
    void payPaymentWithFailedUseBalance() {
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, 1L, 10_000L);
        doThrow(new IllegalArgumentException("잔액이 부족합니다."))
                .when(paymentClient).useBalance(anyLong(), anyLong());

        assertThatThrownBy(() -> paymentService.payPayment(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔액이 부족합니다.");

        assertThat(paymentRepository.findByOrderId(1L)).isEmpty();
        verify(paymentEventPublisher).payFailed(any(PaymentEvent.PayFailed.class));
        verify(paymentEventPublisher, never()).paid(any(PaymentEvent.Paid.class));
        assertThat(events.stream(OutboxEvent.class).count()).isEqualTo(1);
    }

    @DisplayName("쿠폰 사용에 실패하면 결제는 롤백되지만 보상 이벤트는 남는다.")
    @Test
    void payPaymentWithFailedUseCoupon() {
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, 1L, 10_000L);
        doThrow(new IllegalStateException("사용할 수 없는 쿠폰입니다."))
                .when(paymentClient).useCoupon(anyLong());

        assertThatThrownBy(() -> paymentService.payPayment(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("사용할 수 없는 쿠폰입니다.");

        assertThat(paymentRepository.findByOrderId(1L)).isEmpty();
        verify(paymentEventPublisher).payFailed(any(PaymentEvent.PayFailed.class));
        assertThat(events.stream(OutboxEvent.class).count()).isEqualTo(1);
    }

    @DisplayName("같은 주문의 결제 요청이 중복 수신되면 두 번째 요청은 무시된다.")
    @Test
    void payPaymentWithDuplicatedEvent() {
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, 1L, 10_000L);

        paymentService.payPayment(command);
        paymentService.payPayment(command);

        verify(paymentClient, times(1)).useBalance(anyLong(), anyLong());
        verify(paymentEventPublisher, times(1)).paid(any(PaymentEvent.Paid.class));
        assertThat(events.stream(OutboxEvent.class).count()).isEqualTo(1);
    }

    @DisplayName("결제를 취소하면 잔액과 쿠폰이 복구되고 취소 이벤트가 발행된다.")
    @Test
    void cancelPayment() {
        Payment saved = paymentRepository.save(Payment.create(1L, 10_000L));
        doReturn(PaymentInfo.Order.of(1L, 1L, 5L, 10_000L))
                .when(paymentClient).getOrder(1L);

        paymentService.cancelPayment(1L);

        Payment payment = paymentRepository.findById(saved.getId()).orElseThrow();
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELED);
        verify(paymentClient).refundBalance(1L, 10_000L);
        verify(paymentClient).cancelCoupon(5L);
        verify(paymentEventPublisher).canceled(any(PaymentEvent.Canceled.class));
        assertThat(events.stream(OutboxEvent.class).count()).isEqualTo(1);
    }

    @DisplayName("결제 취소 시 쿠폰이 없으면 쿠폰을 취소하지 않는다.")
    @Test
    void cancelPaymentWithoutCoupon() {
        paymentRepository.save(Payment.create(1L, 10_000L));
        doReturn(PaymentInfo.Order.of(1L, 1L, null, 10_000L))
                .when(paymentClient).getOrder(1L);

        paymentService.cancelPayment(1L);

        verify(paymentClient).refundBalance(1L, 10_000L);
        verify(paymentClient, never()).cancelCoupon(anyLong());
    }

    @DisplayName("결제 취소 시 결제가 존재하지 않으면 예외가 발생한다.")
    @Test
    void cancelPaymentWithoutPayment() {
        assertThatThrownBy(() -> paymentService.cancelPayment(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제가 존재하지 않습니다.");

        verify(paymentClient, never()).refundBalance(anyLong(), anyLong());
    }

    @DisplayName("결제 취소 시 잔액 환불에 실패하면 결제 상태가 유지된다.")
    @Test
    void cancelPaymentWithFailedRefundBalance() {
        Payment saved = paymentRepository.save(Payment.create(1L, 10_000L));
        doReturn(PaymentInfo.Order.of(1L, 1L, 5L, 10_000L))
                .when(paymentClient).getOrder(1L);
        doThrow(new IllegalStateException("잔액 환불에 실패했습니다."))
                .when(paymentClient).refundBalance(anyLong(), anyLong());

        assertThatThrownBy(() -> paymentService.cancelPayment(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("잔액 환불에 실패했습니다.");

        Optional<Payment> payment = paymentRepository.findById(saved.getId());
        assertThat(payment).isPresent();
        assertThat(payment.get().getPaymentStatus()).isEqualTo(PaymentStatus.READY);
        verify(paymentEventPublisher, never()).canceled(any(PaymentEvent.Canceled.class));
    }
}
