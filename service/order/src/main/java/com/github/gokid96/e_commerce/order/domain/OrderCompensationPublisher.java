package com.github.gokid96.e_commerce.order.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 보상 이벤트 발행 전용 컴포넌트.
 * 프록시를 거쳐야 전파 속성이 적용되므로 {@code OrderService}와
 * 별도 빈으로 분리한다.
 */
@Component
@RequiredArgsConstructor
public class OrderCompensationPublisher {

    private final OrderEventPublisher orderEventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeFailed(Long orderId) {
        orderEventPublisher.completeFailed(OrderEvent.CompleteFailed.of(orderId));
    }
}