package com.github.gokid96.e_commerce.order.domain;

import com.github.gokid96.e_commerce.common.key.KeyGeneratable;
import com.github.gokid96.e_commerce.common.key.KeyType;

import java.util.List;

public class OrderKey implements KeyGeneratable {

    private final Long orderId;

    private OrderKey(Long orderId) {
        this.orderId = orderId;
    }

    public static OrderKey of(Long orderId) {
        return new OrderKey(orderId);
    }

    @Override
    public KeyType type() {
        return KeyType.ORDER;
    }

    @Override
    public List<String> namespaces() {
        return List.of(orderId.toString());
    }
}