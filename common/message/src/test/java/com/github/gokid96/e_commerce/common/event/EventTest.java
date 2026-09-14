package com.github.gokid96.e_commerce.common.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @DisplayName("이벤트를 생성한다.")
    @Test
    void of() {
        String eventId = "event-id";
        EventType eventType = EventType.ORDER_COMPLETED;
        TestPayload payload = new TestPayload("주문 완료");

        Event<TestPayload> event = Event.of(eventId, eventType, payload);

        assertThat(event.getEventId()).isEqualTo(eventId);
        assertThat(event.getEventType()).isEqualTo(eventType);
        assertThat(event.getPayload()).isEqualTo(payload);
    }

    @DisplayName("이벤트를 JSON 문자열로 변환한다.")
    @Test
    void toJson() {
        Event<TestPayload> event = Event.of("event-id", EventType.ORDER_COMPLETED, new TestPayload("주문 완료"));

        String json = event.toJson();

        assertThat(json).contains("event-id", EventType.ORDER_COMPLETED.name(), "주문 완료");
    }

    @DisplayName("JSON 문자열로부터 이벤트를 복원한다.")
    @Test
    void ofFromJson() {
        String json = Event.of("event-id", EventType.ORDER_COMPLETED, new TestPayload("주문 완료")).toJson();

        Event<TestPayload> event = Event.of(json, TestPayload.class);

        assertThat(event.getEventId()).isEqualTo("event-id");
        assertThat(event.getEventType()).isEqualTo(EventType.ORDER_COMPLETED);
        assertThat(event.getPayload().getData()).isEqualTo("주문 완료");
    }

    @DisplayName("페이로드에 정의되지 않은 필드가 있어도 이벤트를 복원한다.")
    @Test
    void ofFromJsonWithUnknownField() {
        String json = """
                {
                    "eventId": "event-id",
                    "eventType": "ORDER_COMPLETED",
                    "payload": {
                        "data": "주문 완료",
                        "unknown": "무시되어야 한다"
                    }
                }
                """;

        Event<TestPayload> event = Event.of(json, TestPayload.class);

        assertThat(event.getPayload().getData()).isEqualTo("주문 완료");
    }

    @DisplayName("JSON 문자열이 올바르지 않으면 null 을 반환한다.")
    @Test
    void ofFromInvalidJson() {
        Event<TestPayload> event = Event.of("잘못된 JSON", TestPayload.class);

        assertThat(event).isNull();
    }

    @Getter
    @NoArgsConstructor
    static class TestPayload {

        private String data;

        TestPayload(String data) {
            this.data = data;
        }
    }
}
