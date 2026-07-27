package com.github.gokid96.e_commerce.order.interfaces.event;

import com.github.gokid96.e_commerce.order.domain.OrderEvent;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderService orderService;

    @Async
    @EventListener
    public void handle(OrderEvent.Failed event) {
        log.info("주문 실패 이벤트 수신 - 주문 취소");
        orderService.cancelOrder(event.getOrderId());
    }
}