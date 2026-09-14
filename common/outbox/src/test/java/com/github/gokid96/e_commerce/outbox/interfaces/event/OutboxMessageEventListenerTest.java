package com.github.gokid96.e_commerce.outbox.interfaces.event;

import com.github.gokid96.e_commerce.outbox.domain.OutboxService;
import com.github.gokid96.e_commerce.outbox.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class OutboxMessageEventListenerTest extends MockTestSupport {

    @InjectMocks
    private OutboxMessageEventListener outboxMessageEventListener;

    @Mock
    private OutboxService outboxService;

    @DisplayName("발행된 이벤트를 되받아 해당 아웃박스를 삭제한다.")
    @Test
    void handle() {
        String message = """
                {
                    "eventId": "fee5d5ce-cdf7-4797-8baa-0cad19f80153",
                    "eventType": "ORDER_CREATED",
                    "payload": {
                        "orderId": 1,
                        "userId": 1,
                        "userCouponId": null,
                        "totalPrice": 20000
                    }
                }
                """;
        Acknowledgment ack = mock(Acknowledgment.class);

        outboxMessageEventListener.handle(message, ack);

        verify(outboxService).clearOutbox("fee5d5ce-cdf7-4797-8baa-0cad19f80153");
        verify(ack).acknowledge();
    }

    @DisplayName("메시지를 해석할 수 없으면 삭제와 커밋을 수행하지 않는다.")
    @Test
    void handleWithInvalidMessage() {
        Acknowledgment ack = mock(Acknowledgment.class);

        try {
            outboxMessageEventListener.handle("잘못된 JSON", ack);
        } catch (Exception ignored) {
            // 예외 전파는 컨슈머 에러 핸들러(재시도 후 DLT)의 책임이므로 여기서는 확인하지 않는다.
        }

        verify(outboxService, never()).clearOutbox(anyString());
        verify(ack, never()).acknowledge();
    }
}
