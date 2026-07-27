package com.github.gokid96.e_commerce.message.interfaces.event;

import com.github.gokid96.e_commerce.message.domain.MessageService;
import com.github.gokid96.e_commerce.order.domain.OrderEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageOrderEventListenerUnitTest {

    @InjectMocks
    private MessageOrderEventListener messageOrderEventListener;

    @Mock
    private MessageService messageService;

    @DisplayName("주문 완료 시, 외부 데이터 플랫폼으로 주문 정보를 전송한다.")
    @Test
    void handleCompleted() {
        // given
        OrderEvent.Completed event = mock(OrderEvent.Completed.class);

        // when
        messageOrderEventListener.handle(event);

        // then
        verify(messageService, times(1)).sendOrder(any());
    }
}