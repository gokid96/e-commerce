package com.github.gokid96.e_commerce.outbox.interfaces.event;

import com.github.gokid96.e_commerce.common.event.Event;
import com.github.gokid96.e_commerce.common.event.EventType.Topic;
import com.github.gokid96.e_commerce.outbox.domain.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static com.github.gokid96.e_commerce.common.event.EventType.GroupId;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxMessageEventListener {

    private final OutboxService outboxService;

    @KafkaListener(topics = {
            Topic.COUPON_PUBLISH_REQUESTED,
            Topic.ORDER_COMPLETE_FAILED,
            Topic.ORDER_COMPLETED,
            Topic.ORDER_CREATED,
            Topic.PAYMENT_PAID,
            Topic.PAYMENT_FAILED,
            Topic.PAYMENT_CANCELED,
    }, groupId = GroupId.OUTBOX)
    public void handle(String message, Acknowledgment ack) {
        log.info("아웃 박스 이벤트 수신 {}", message);

        Event<?> event = Event.of(message, Object.class);
        outboxService.clearOutbox(event.getEventId());
        ack.acknowledge();
    }
}