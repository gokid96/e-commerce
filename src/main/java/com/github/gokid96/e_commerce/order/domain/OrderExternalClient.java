package com.github.gokid96.e_commerce.order.domain;

public interface OrderExternalClient {
    void sendOrderMessage(Order order);
}