package com.github.gokid96.e_commerce.payment.infrastructure.event;

import com.github.gokid96.e_commerce.common.event.EventType;
import com.github.gokid96.e_commerce.common.outbox.OutboxEventPublisher;
import com.github.gokid96.e_commerce.payment.domain.PaymentEvent;
import com.github.gokid96.e_commerce.payment.domain.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentSpringEventPublisher implements PaymentEventPublisher {

    private final OutboxEventPublisher outboxEventPublisher;

    @Override
    public void paid(PaymentEvent.Paid event) {
        outboxEventPublisher.publishEvent(EventType.PAYMENT_PAID, event.getOrderId(), event);
    }

    @Override
    public void payFailed(PaymentEvent.PayFailed event) {
        outboxEventPublisher.publishEvent(EventType.PAYMENT_FAILED, event.getOrderId(), event);
    }

    @Override
    public void canceled(PaymentEvent.Canceled event) {
        outboxEventPublisher.publishEvent(EventType.PAYMENT_CANCELED, event.getOrderId(), event);
    }
}
