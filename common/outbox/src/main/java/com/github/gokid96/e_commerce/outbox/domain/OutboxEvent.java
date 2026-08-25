package com.github.gokid96.e_commerce.outbox.domain;

import lombok.Getter;

@Getter
public class OutboxEvent {

    private final Outbox outbox;

    private OutboxEvent(Outbox outbox) {
        this.outbox = outbox;
    }

    public static OutboxEvent of(Outbox outbox) {
        return new OutboxEvent(outbox);
    }
}