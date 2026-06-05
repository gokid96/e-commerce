package com.github.gokid96.e_commerce.order.domain;

import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {

    Order save(Order order);

    Order findById(Long orderId);

}
