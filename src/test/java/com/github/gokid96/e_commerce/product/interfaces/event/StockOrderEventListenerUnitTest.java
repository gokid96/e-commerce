package com.github.gokid96.e_commerce.product.interfaces.event;

import com.github.gokid96.e_commerce.order.domain.OrderEvent;
import com.github.gokid96.e_commerce.order.domain.OrderProcesses;
import com.github.gokid96.e_commerce.product.domain.stock.StockEvent;
import com.github.gokid96.e_commerce.product.domain.stock.StockEventPublisher;
import com.github.gokid96.e_commerce.product.domain.stock.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockOrderEventListenerUnitTest {

    @InjectMocks
    private StockOrderEventListener eventListener;

    @Mock
    @Mock
    private StockService stockService;

    @Mock
    private StockEventPublisher eventPublisher;

    @DisplayName("주문 생성 시, 재고 차감 이벤트를 발행한다.")
    @Test
    void handleCreated() {
        OrderEvent.Created event = mock(OrderEvent.Created.class);

        eventListener.handle(event);

        verify(stockService, times(1)).deductStock(any());
        verify(eventPublisher, times(1)).deducted(any(StockEvent.Deducted.class));
    }

    @DisplayName("주문 생성 시, 재고 차감 실패하면 실패 이벤트를 발행한다.")
    @Test
    void handleCreatedWithFailed() {
        OrderEvent.Created event = mock(OrderEvent.Created.class);

        doThrow(new RuntimeException("재고 차감 실패"))
                .when(stockService).deductStock(any());

        eventListener.handle(event);

        verify(eventPublisher, times(1)).deductFailed(any(StockEvent.DeductFailed.class));
    }

    @DisplayName("주문 실패 시, 재고를 복구한다.")
    @Test
    void handleFailed() {
        OrderEvent.Failed event = mock(OrderEvent.Failed.class);
        OrderProcesses processes = mock(OrderProcesses.class);

        when(event.getProcesses()).thenReturn(processes);
        when(processes.isSuccess(any())).thenReturn(true);

        eventListener.handle(event);

        verify(stockService, times(1)).restoreStock(any());
    }
}