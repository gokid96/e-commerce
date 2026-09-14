package com.github.gokid96.e_commerce.outbox.domain;

import com.github.gokid96.e_commerce.common.event.EventType;
import com.github.gokid96.e_commerce.outbox.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class OutboxServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private OutboxService outboxService;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private OutboxEventProducer outboxEventProducer;

    @Captor
    private ArgumentCaptor<LocalDateTime> createdAtCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @DisplayName("아웃박스를 저장한다.")
    @Test
    void createOutbox() {
        Outbox outbox = Outbox.create("event-id", EventType.ORDER_CREATED, 1L, "{\"orderId\":1}");
        given(outboxRepository.save(outbox)).willReturn(outbox);

        Outbox result = outboxService.createOutbox(outbox);

        assertThat(result).isSameAs(outbox);
        verify(outboxRepository).save(outbox);
    }

    @DisplayName("아웃박스 이벤트를 발행한다.")
    @Test
    void produceEvent() {
        Outbox outbox = Outbox.create("event-id", EventType.ORDER_CREATED, 1L, "{\"orderId\":1}");

        outboxService.produceEvent(outbox);

        verify(outboxEventProducer).produceEvent(outbox);
    }

    @DisplayName("발행되지 않고 남은 아웃박스를 모두 재발행한다.")
    @Test
    void publishPendingEvent() {
        given(outboxRepository.findPendingEvent(any(LocalDateTime.class), any(Pageable.class)))
                .willReturn(List.of(
                        Outbox.create("event-id-1", EventType.ORDER_CREATED, 1L, "{\"orderId\":1}"),
                        Outbox.create("event-id-2", EventType.ORDER_CREATED, 2L, "{\"orderId\":2}")
                ));

        outboxService.publishPendingEvent();

        verify(outboxEventProducer, times(2)).produceEvent(any(Outbox.class));
    }

    @DisplayName("재발행 대상은 생성된 지 10초가 지난 아웃박스 100건으로 제한한다.")
    @Test
    void publishPendingEventWithLimit() {
        LocalDateTime before = LocalDateTime.now();
        given(outboxRepository.findPendingEvent(any(LocalDateTime.class), any(Pageable.class)))
                .willReturn(List.of());

        outboxService.publishPendingEvent();

        verify(outboxRepository).findPendingEvent(createdAtCaptor.capture(), pageableCaptor.capture());
        assertThat(createdAtCaptor.getValue())
                .isAfterOrEqualTo(before.minusSeconds(10))
                .isBeforeOrEqualTo(LocalDateTime.now().minusSeconds(10));
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(100);
    }

    @DisplayName("재발행 대상이 없으면 아무것도 발행하지 않는다.")
    @Test
    void publishPendingEventWithoutTarget() {
        given(outboxRepository.findPendingEvent(any(LocalDateTime.class), any(Pageable.class)))
                .willReturn(List.of());

        outboxService.publishPendingEvent();

        verify(outboxEventProducer, never()).produceEvent(any(Outbox.class));
    }

    @DisplayName("발행이 확인된 아웃박스를 삭제한다.")
    @Test
    void clearOutbox() {
        outboxService.clearOutbox("event-id");

        verify(outboxRepository).deleteByEventId("event-id");
    }
}
