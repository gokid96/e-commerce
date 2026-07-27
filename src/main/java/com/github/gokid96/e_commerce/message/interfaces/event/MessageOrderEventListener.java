package com.github.gokid96.e_commerce.message.interfaces.event;

import com.github.gokid96.e_commerce.message.domain.MessageCommand;
import com.github.gokid96.e_commerce.message.domain.MessageService;
import com.github.gokid96.e_commerce.order.domain.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageOrderEventListener {

    private final MessageService messageService;

    @Async
    @EventListener
    public void handle(OrderEvent.Completed event) {
        log.info("주문 완료 이벤트 수신 - 외부 데이터 플랫폼 주문정보 전송");
        messageService.sendOrder(MessageCommand.Order.of(event));
    }
}