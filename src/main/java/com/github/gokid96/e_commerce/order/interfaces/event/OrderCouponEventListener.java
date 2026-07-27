package com.github.gokid96.e_commerce.order.interfaces.event;

import com.github.gokid96.e_commerce.coupon.domain.CouponEvent;
import com.github.gokid96.e_commerce.order.domain.OrderCommand;
import com.github.gokid96.e_commerce.order.domain.OrderService;
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
public class OrderCouponEventListener {

    private final OrderService orderService;

    @Async
    @EventListener
    public void handle(CouponEvent.Used event) {
        log.info("쿠폰 사용 성공 이벤트 수신 - 주문 프로세스 성공 갱신");
        OrderCommand.Process command = OrderCommand.Process.ofCouponUsed(event.getOrderId(), SUCCESS);
        orderService.updateProcess(command);
    }

    @Async
    @EventListener
    public void handle(CouponEvent.UseFailed event) {
        log.info("쿠폰 사용 실패 이벤트 수신 - 주문 프로세스 실패 갱신");
        OrderCommand.Process command = OrderCommand.Process.ofCouponUsed(event.getOrderId(), FAILED);
        orderService.updateProcess(command);
    }
}