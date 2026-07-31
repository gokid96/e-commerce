package com.github.gokid96.e_commerce.outbox.infrastructure.event;

import com.github.gokid96.e_commerce.common.message.MessageProducer;
import com.github.gokid96.e_commerce.outbox.domain.Outbox;
import com.github.gokid96.e_commerce.outbox.domain.OutboxEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventProducerImpl implements OutboxEventProducer {

    private final MessageProducer messageProducer;

    @Override
    public void produceEvent(Outbox outbox) {
        messageProducer.send(outbox);
    }
}