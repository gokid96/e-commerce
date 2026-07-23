package com.github.gokid96.e_commerce.order.domain;

public interface OrderEventPublisher {

    void paid(OrderEvent.Paid event);
}