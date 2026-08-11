package com.github.gokid96.e_commerce.common.outbox;

import com.github.gokid96.e_commerce.common.event.EventType;

public interface OutboxEventPublisher {

    <T> void publishEvent(EventType type, Long partitionKey, T payload);
}