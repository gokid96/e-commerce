package com.github.gokid96.e_commerce.payment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 보상 이벤트 발행 전용 컴포넌트.
 * 프록시를 거쳐야 전파 속성이 적용되므로 {@code PaymentService}와
 * 별도 빈으로 분리한다.
 */
@Component
@RequiredArgsConstructor
public class PaymentCompensationPublisher {

    private final PaymentEventPublisher paymentEventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void payFailed(Long orderId) {
        paymentEventPublisher.payFailed(PaymentEvent.PayFailed.of(orderId));
    }
}