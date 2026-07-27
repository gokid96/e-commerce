package com.github.gokid96.e_commerce.balance.domain;

public interface BalanceEventPublisher {

    void used(BalanceEvent.Used event);

    void useFailed(BalanceEvent.UseFailed event);
}