package com.github.gokid96.e_commerce.message.interfaces.event;

import com.github.gokid96.e_commerce.message.domain.MessageCommand;
import com.github.gokid96.e_commerce.message.domain.MessageService;
import com.github.gokid96.e_commerce.order.domain.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final MessageService messageService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaidEvent(OrderEvent.Paid event) {
        messageService.sendOrder(MessageCommand.Order.of(event));
    }
}