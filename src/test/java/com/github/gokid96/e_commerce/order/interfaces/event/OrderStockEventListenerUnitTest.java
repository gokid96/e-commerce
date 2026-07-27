package com.github.gokid96.e_commerce.order.interfaces.event;

import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.product.domain.stock.StockEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStockEventListenerUnitTest {

    @InjectMocks
    private OrderStockEventListener eventListener;

    @Mock
    private OrderService orderService;

    @DisplayName("재고 차감 성공 시, 주문 프로세스를 성공 갱신한다.")
    @Test
    void handleDeducted() {
        // given
        StockEvent.Deducted event = mock(StockEvent.Deducted.class);

        // when
        eventListener.handle(event);

        // then
        verify(orderService, times(1)).updateProcess(any());
    }

    @DisplayName("재고 차감 실패 시, 주문 프로세스를 실패 갱신한다.")
    @Test
    void handleDeductFailed() {
        // given
        StockEvent.DeductFailed event = mock(StockEvent.DeductFailed.class);

        // when
        eventListener.handle(event);

        // then
        verify(orderService, times(1)).updateProcess(any());
    }
}