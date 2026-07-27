package com.github.gokid96.e_commerce.order.domain;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    public OrderInfo.Order createOrder(OrderCommand.Create command) {
        List<OrderProduct> orderProducts = command.getProducts().stream()
                .map(this::toOrderProduct)
                .toList();

        Order order = Order.create(
                command.getUserId(),
                command.getUserCouponId(),
                command.getDiscountRate(),
                orderProducts
        );
        orderRepository.save(order);

        return OrderInfo.Order.of(order);
    }

    @Transactional
    public void paidOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));

        order.paid(LocalDateTime.now());
        orderEventPublisher.paid(OrderEvent.Paid.of(order));
    }

    private OrderProduct toOrderProduct(OrderCommand.OrderProduct command) {
        return OrderProduct.create(
                command.getProductId(),
                command.getProductName(),
                command.getProductPrice(),
                command.getQuantity()
        );
    }
}