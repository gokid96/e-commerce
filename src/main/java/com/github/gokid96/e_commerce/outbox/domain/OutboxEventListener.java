package com.github.gokid96.e_commerce.outbox.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventListener {

    private final OutboxService outboxService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createOutbox(OutboxEvent event) {
        log.info("createOutbox - 아웃박스 이벤트 수신: {}", event.getOutbox().getTopic());
        outboxService.createOutbox(event.getOutbox());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void produceEvent(OutboxEvent event) {
        log.info("produceEvent - 아웃박스 이벤트 수신: {}", event.getOutbox().getTopic());
        outboxService.produceEvent(event.getOutbox());
    }
}