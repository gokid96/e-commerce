package com.github.gokid96.e_commerce.order.infrastructure.event;

import com.github.gokid96.e_commerce.common.event.EventType;
import com.github.gokid96.e_commerce.common.outbox.OutboxEventPublisher;
import com.github.gokid96.e_commerce.order.domain.OrderEvent;
import com.github.gokid96.e_commerce.order.domain.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSpringEventPublisher implements OrderEventPublisher {

    private final OutboxEventPublisher outboxEventPublisher;

    @Override
    public void created(OrderEvent.Created event) {
        outboxEventPublisher.publishEvent(EventType.ORDER_CREATED, event.getOrderId(), event);
    }

    @Override
    public void completed(OrderEvent.Completed event) {
        outboxEventPublisher.publishEvent(EventType.ORDER_COMPLETED, event.getOrderId(), event);
    }

    @Override
    public void completeFailed(OrderEvent.CompleteFailed event) {
        outboxEventPublisher.publishEvent(EventType.ORDER_COMPLETE_FAILED, event.getOrderId(), event);
    }
}
