package com.github.gokid96.e_commerce.outbox.domain;

import lombok.Getter;

public class OutboxEvent {

    @Getter
    public static class Auto {

        private final Outbox outbox;

        private Auto(Outbox outbox) {
            this.outbox = outbox;
        }

        public static Auto of(Outbox outbox) {
            return new Auto(outbox);
        }
    }

    @Getter
    public static class Manual {

        private final Outbox outbox;

        private Manual(Outbox outbox) {
            this.outbox = outbox;
        }

        public static Manual of(Outbox outbox) {
            return new Manual(outbox);
        }
    }
}