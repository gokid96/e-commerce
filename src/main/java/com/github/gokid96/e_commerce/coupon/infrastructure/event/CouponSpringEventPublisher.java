package com.github.gokid96.e_commerce.coupon.infrastructure.event;

import com.github.gokid96.e_commerce.common.event.EventType;
import com.github.gokid96.e_commerce.common.message.DefaultMessage;
import com.github.gokid96.e_commerce.common.message.Message;
import com.github.gokid96.e_commerce.common.message.MessageProducer;
import com.github.gokid96.e_commerce.coupon.domain.CouponEvent;
import com.github.gokid96.e_commerce.coupon.domain.CouponEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponSpringEventPublisher implements CouponEventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final MessageProducer messageProducer;

    @Override
    public void publishRequested(CouponEvent.PublishRequested event) {
        Message message = DefaultMessage.of(EventType.COUPON_PUBLISH_REQUESTED, event.getCouponId(), event);
        messageProducer.send(message);
    }

    @Override
    public void published(CouponEvent.Published event) {
        eventPublisher.publishEvent(event);
    }
}