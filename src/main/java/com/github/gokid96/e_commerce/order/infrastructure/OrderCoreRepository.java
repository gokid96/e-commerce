package com.github.gokid96.e_commerce.order.infrastructure;

import com.github.gokid96.e_commerce.order.domain.Order;
import com.github.gokid96.e_commerce.order.domain.OrderCommand;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.domain.OrderRepository;
import com.github.gokid96.e_commerce.order.infrastructure.jpa.OrderJpaRepository;
import com.github.gokid96.e_commerce.order.interfaces.OrderQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderCoreRepository implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderQueryDslRepository orderQueryDslRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return orderJpaRepository.findById(orderId);
    }

    @Override
    public List<OrderInfo.PaidProduct> findPaidProducts(OrderCommand.PaidProducts command) {
        return orderQueryDslRepository.findPaidProducts(command);
    }
}