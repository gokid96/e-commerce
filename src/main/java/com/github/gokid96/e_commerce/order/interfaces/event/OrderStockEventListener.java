package com.github.gokid96.e_commerce.order.interfaces.event;

import com.github.gokid96.e_commerce.order.domain.OrderCommand;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.product.domain.stock.StockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.github.gokid96.e_commerce.order.domain.OrderProcessStatus.FAILED;
import static com.github.gokid96.e_commerce.order.domain.OrderProcessStatus.SUCCESS;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStockEventListener {

    private final OrderService orderService;

    @Async
    @EventListener
    public void handle(StockEvent.Deducted event) {
        log.info("재고 차감 성공 이벤트 수신 - 주문 프로세스 성공 갱신");
        OrderCommand.Process command = OrderCommand.Process.ofStockDeducted(event.getOrderId(), SUCCESS);
        orderService.updateProcess(command);
    }

    @Async
    @EventListener
    public void handle(StockEvent.DeductFailed event) {
        log.info("재고 차감 실패 이벤트 수신 - 주문 프로세스 실패 갱신");
        OrderCommand.Process command = OrderCommand.Process.ofStockDeducted(event.getOrderId(), FAILED);
        orderService.updateProcess(command);
    }
}