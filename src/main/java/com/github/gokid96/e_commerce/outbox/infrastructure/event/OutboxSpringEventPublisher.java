package com.github.gokid96.e_commerce.outbox.infrastructure.event;

import com.github.gokid96.e_commerce.common.event.Event;
import com.github.gokid96.e_commerce.common.event.EventType;
import com.github.gokid96.e_commerce.common.outbox.OutboxEventPublisher;
import com.github.gokid96.e_commerce.outbox.domain.Outbox;
import com.github.gokid96.e_commerce.outbox.domain.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxSpringEventPublisher implements OutboxEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public <T> void publishEvent(EventType type, Long partitionKey, T payload) {
        Outbox outbox = create(type, partitionKey, payload);
        OutboxEvent.Auto event = OutboxEvent.Auto.of(outbox);

        eventPublisher.publishEvent(event);
    }

    @Override
    public <T> void publishManualEvent(EventType type, Long partitionKey, T payload) {
        Outbox outbox = create(type, partitionKey, payload);
        OutboxEvent.Manual event = OutboxEvent.Manual.of(outbox);

        eventPublisher.publishEvent(event);
    }

    private <T> Outbox create(EventType type, Long partitionKey, T payload) {
        String eventId = UUID.randomUUID().toString();
        return Outbox.create(
                eventId,
                type,
                partitionKey,
                Event.of(eventId, type, payload).toJson()
        );
    }
}