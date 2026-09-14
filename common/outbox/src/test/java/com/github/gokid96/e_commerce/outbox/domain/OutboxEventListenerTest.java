package com.github.gokid96.e_commerce.outbox.domain;

import com.github.gokid96.e_commerce.common.event.EventType;
import com.github.gokid96.e_commerce.outbox.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class OutboxEventListenerTest extends MockTestSupport {

    @InjectMocks
    private OutboxEventListener outboxEventListener;

    @Mock
    private OutboxService outboxService;

    @DisplayName("커밋 전에는 아웃박스를 저장하고 발행하지 않는다.")
    @Test
    void createOutbox() {
        OutboxEvent event = OutboxEvent.of(
                Outbox.create("event-id", EventType.ORDER_CREATED, 1L, "{\"orderId\":1}"));

        outboxEventListener.createOutbox(event);

        verify(outboxService).createOutbox(event.getOutbox());
        verify(outboxService, never()).produceEvent(event.getOutbox());
    }

    @DisplayName("커밋 후에는 아웃박스를 발행하고 저장하지 않는다.")
    @Test
    void produceEvent() {
        OutboxEvent event = OutboxEvent.of(
                Outbox.create("event-id", EventType.ORDER_CREATED, 1L, "{\"orderId\":1}"));

        outboxEventListener.produceEvent(event);

        verify(outboxService).produceEvent(event.getOutbox());
        verify(outboxService, never()).createOutbox(event.getOutbox());
    }
}
