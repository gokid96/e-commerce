package com.github.gokid96.e_commerce.order.infrastructure;

import com.github.gokid96.e_commerce.order.domain.Order;
import com.github.gokid96.e_commerce.order.domain.OrderRepository;
import com.github.gokid96.e_commerce.order.infrastructure.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderCoreRepository implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return orderJpaRepository.findById(orderId);
    }
}
