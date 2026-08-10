package com.github.gokid96.e_commerce.payment.interfaces.event;

import com.github.gokid96.e_commerce.common.event.Event;
import com.github.gokid96.e_commerce.common.event.EventType.GroupId;
import com.github.gokid96.e_commerce.common.event.EventType.Topic;
import com.github.gokid96.e_commerce.order.domain.OrderEvent;
import com.github.gokid96.e_commerce.payment.domain.PaymentCommand;
import com.github.gokid96.e_commerce.payment.domain.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOrderMessageEventListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = Topic.ORDER_CREATED, groupId = GroupId.PAYMENT)
    @KafkaListener(topics = Topic.ORDER_CREATED, groupId = GroupId.PAYMENT, concurrency = "3")
    public void handleOrderCreated(String message, Acknowledgment ack) {
        log.info("주문 생성 이벤트 수신 {}", message);

        Event<OrderEvent.Created> event = Event.of(message, OrderEvent.Created.class);
        OrderEvent.Created payload = event.getPayload();

        paymentService.payPayment(
                PaymentCommand.Payment.of(
                        payload.getOrderId(),
                        payload.getUserId(),
                        payload.getUserCouponId(),
                        payload.getTotalPrice()
                )
        );

        ack.acknowledge();
    }
}
