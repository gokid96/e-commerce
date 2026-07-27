package com.github.gokid96.e_commerce.balance.infrastructure.event;

import com.github.gokid96.e_commerce.balance.domain.BalanceEvent;
import com.github.gokid96.e_commerce.balance.domain.BalanceEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceSpringEventPublisher implements BalanceEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void used(BalanceEvent.Used event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void useFailed(BalanceEvent.UseFailed event) {
        eventPublisher.publishEvent(event);
    }
}