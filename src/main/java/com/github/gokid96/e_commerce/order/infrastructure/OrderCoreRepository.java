package com.github.gokid96.e_commerce.order.infrastructure;

import com.github.gokid96.e_commerce.order.domain.Order;
import com.github.gokid96.e_commerce.order.domain.OrderCommand;
import com.github.gokid96.e_commerce.order.domain.OrderKey;
import com.github.gokid96.e_commerce.order.domain.OrderProcess;
import com.github.gokid96.e_commerce.order.domain.OrderRepository;
import com.github.gokid96.e_commerce.order.infrastructure.jpa.OrderJpaRepository;
import com.github.gokid96.e_commerce.order.infrastructure.redis.OrderRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderCoreRepository implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderRedisRepository orderRedisRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return orderJpaRepository.findById(orderId);
    }

    @Override
    public void updateProcess(OrderCommand.Process command) {
        orderRedisRepository.updateProcess(command);
    }

    @Override
    public List<OrderProcess> getProcess(OrderKey key) {
        return orderRedisRepository.getProcess(key);
    }
}