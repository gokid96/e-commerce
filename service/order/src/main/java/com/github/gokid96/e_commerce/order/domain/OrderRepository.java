package com.github.gokid96.e_commerce.order.domain;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long orderId);
}
