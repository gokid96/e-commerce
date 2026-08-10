package com.github.gokid96.e_commerce.order.interfaces.event;

import com.github.gokid96.e_commerce.common.event.Event;
import com.github.gokid96.e_commerce.common.event.EventType.GroupId;
import com.github.gokid96.e_commerce.common.event.EventType.Topic;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.payment.domain.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentMessageEventListener {

    private final OrderService orderService;

    @KafkaListener(topics = Topic.PAYMENT_PAID, groupId = GroupId.ORDER)
    @KafkaListener(topics = Topic.PAYMENT_PAID, groupId = GroupId.ORDER, concurrency = "3")
    public void handlePaymentPaid(String message, Acknowledgment ack) {
        log.info("결제 완료 이벤트 수신 {}", message);

        Event<PaymentEvent.Paid> event = Event.of(message, PaymentEvent.Paid.class);
        PaymentEvent.Paid payload = event.getPayload();

        orderService.completedOrder(payload.getOrderId());

        ack.acknowledge();
    }

    @KafkaListener(topics = Topic.PAYMENT_FAILED, groupId = GroupId.ORDER)
    public void handlePaymentFailed(String message, Acknowledgment ack) {
        log.info("결제 실패 이벤트 수신 {}", message);

        Event<PaymentEvent.PayFailed> event = Event.of(message, PaymentEvent.PayFailed.class);
        PaymentEvent.PayFailed payload = event.getPayload();

        orderService.cancelOrder(payload.getOrderId());

        ack.acknowledge();
    }

    @KafkaListener(topics = Topic.PAYMENT_CANCELED, groupId = GroupId.ORDER)
    public void handlePaymentCanceled(String message, Acknowledgment ack) {
        log.info("결제 취소 이벤트 수신 {}", message);

        Event<PaymentEvent.Canceled> event = Event.of(message, PaymentEvent.Canceled.class);
        PaymentEvent.Canceled payload = event.getPayload();

        orderService.cancelOrder(payload.getOrderId());

        ack.acknowledge();
    }
}
