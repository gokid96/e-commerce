package com.github.gokid96.e_commerce.order.domain;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long orderId);

}
