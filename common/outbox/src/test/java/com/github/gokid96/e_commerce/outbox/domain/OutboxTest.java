package com.github.gokid96.e_commerce.outbox.domain;

import com.github.gokid96.e_commerce.common.event.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxTest {

    @DisplayName("아웃박스를 생성한다.")
    @Test
    void create() {
        Outbox outbox = Outbox.create("event-id", EventType.ORDER_CREATED, 1L, "{\"orderId\":1}");

        assertThat(outbox.getEventId()).isEqualTo("event-id");
        assertThat(outbox.getEventType()).isEqualTo(EventType.ORDER_CREATED);
        assertThat(outbox.getPartitionKey()).isEqualTo(1L);
        assertThat(outbox.getPayload()).isEqualTo("{\"orderId\":1}");
        assertThat(outbox.getCreatedAt()).isNotNull();
    }

    @DisplayName("아웃박스의 토픽은 이벤트 타입의 토픽을 따른다.")
    @Test
    void getTopic() {
        Outbox outbox = Outbox.create("event-id", EventType.PAYMENT_PAID, 1L, "{}");

        assertThat(outbox.getTopic()).isEqualTo(EventType.Topic.PAYMENT_PAID);
    }

    @DisplayName("아웃박스의 메시지 키는 파티션 키를 문자열로 변환한 값이다.")
    @Test
    void getKey() {
        Outbox outbox = Outbox.create("event-id", EventType.ORDER_CREATED, 42L, "{}");

        assertThat(outbox.getKey()).isEqualTo("42");
    }
}
