package com.github.gokid96.e_commerce.balance.interfaces.event;

import com.github.gokid96.e_commerce.balance.domain.BalanceEvent;
import com.github.gokid96.e_commerce.balance.domain.BalanceEventPublisher;
import com.github.gokid96.e_commerce.balance.domain.BalanceService;
import com.github.gokid96.e_commerce.order.domain.OrderEvent;
import com.github.gokid96.e_commerce.order.domain.OrderProcesses;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceOrderEventListenerUnitTest {

    @InjectMocks
    private BalanceOrderEventListener eventListener;

    @Mock
    private BalanceService balanceService;

    @Mock
    private BalanceEventPublisher eventPublisher;

    @DisplayName("주문 생성 시, 잔액 사용 이벤트를 발행한다.")
    @Test
    void handleCreated() {
        OrderEvent.Created event = mock(OrderEvent.Created.class);

        eventListener.handle(event);

        verify(eventPublisher, times(1)).used(any(BalanceEvent.Used.class));
    }

    @DisplayName("주문 생성 시, 잔액 사용 실패하면 실패 이벤트를 발행한다.")
    @Test
    void handleCreatedWithFailed() {
        OrderEvent.Created event = mock(OrderEvent.Created.class);

        doThrow(new RuntimeException("잔액 사용 실패"))
                .when(balanceService).useBalance(any());

        eventListener.handle(event);

        verify(eventPublisher, times(1)).useFailed(any(BalanceEvent.UseFailed.class));
    }

    @DisplayName("주문 실패 시, 잔액을 환불한다.")
    @Test
    void handleFailed() {
        OrderEvent.Failed event = mock(OrderEvent.Failed.class);
        OrderProcesses processes = mock(OrderProcesses.class);

        when(event.getProcesses()).thenReturn(processes);
        when(processes.isSuccess(any())).thenReturn(true);

        eventListener.handle(event);

        verify(balanceService, times(1)).refundBalance(any());
    }
}