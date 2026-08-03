package com.github.gokid96.e_commerce.coupon.infrastructure.event;

import com.github.gokid96.e_commerce.common.event.EventType;
import com.github.gokid96.e_commerce.common.outbox.OutboxEventPublisher;
import com.github.gokid96.e_commerce.coupon.domain.CouponEvent;
import com.github.gokid96.e_commerce.coupon.domain.CouponEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponSpringEventPublisher implements CouponEventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final OutboxEventPublisher outboxEventPublisher;

    @Override
    public void publishRequested(CouponEvent.PublishRequested event) {
        outboxEventPublisher.publishManualEvent(EventType.COUPON_PUBLISH_REQUESTED, event.getCouponId(), event);
    }

    @Override
    public void published(CouponEvent.Published event) {
        eventPublisher.publishEvent(event);
    }
}
