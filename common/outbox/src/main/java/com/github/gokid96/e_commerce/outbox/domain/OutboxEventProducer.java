package com.github.gokid96.e_commerce.outbox.domain;

public interface OutboxEventProducer {

    void produceEvent(Outbox outbox);
}