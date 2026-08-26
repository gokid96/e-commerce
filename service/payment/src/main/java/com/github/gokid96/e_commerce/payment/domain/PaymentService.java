package com.github.gokid96.e_commerce.payment.domain;

import com.github.gokid96.e_commerce.balance.domain.BalanceCommand;
import com.github.gokid96.e_commerce.balance.domain.BalanceService;
import com.github.gokid96.e_commerce.coupon.domain.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final BalanceService balanceService;
    private final CouponService couponService;

    @Transactional
    public void payPayment(PaymentCommand.Payment command) {
        try {
            Payment payment = Payment.create(command.getOrderId(), command.getAmount());
            payment.pay();

            balanceService.useBalance(BalanceCommand.Use.of(command.getUserId(), command.getAmount()));
            Optional.ofNullable(command.getUserCouponId())
                    .ifPresent(couponService::useUserCoupon);

            paymentRepository.save(payment);

            paymentEventPublisher.paid(
                    PaymentEvent.Paid.of(
                            payment.getId(),
                            payment.getOrderId(),
                            command.getUserId(),
                            payment.getAmount()
                    )
            );
        } catch (Exception e) {
            paymentEventPublisher.payFailed(PaymentEvent.PayFailed.of(command.getOrderId()));
            throw e;
        }
    }

    @Transactional
    public void cancelPayment(Long paymentId) {
        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new IllegalArgumentException("결제가 존재하지 않습니다."));
            payment.cancel();
            paymentRepository.save(payment);

            paymentEventPublisher.canceled(PaymentEvent.Canceled.of(payment.getOrderId()));
        } catch (Exception e) {
            log.error("결제 취소 실패 - paymentId: {}", paymentId, e);
            throw e;
        }
    }
}
