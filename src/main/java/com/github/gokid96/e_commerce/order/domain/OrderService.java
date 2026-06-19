package com.github.gokid96.e_commerce.order.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderExternalClient orderExternalClient;

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

    public void paidOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));

        order.paid(LocalDateTime.now());
        orderExternalClient.sendOrderMessage(order);
    }

    public OrderInfo.PaidProducts getPaidProducts(OrderCommand.DateQuery command) {
        OrderCommand.PaidProducts queryCommand = command.toPaidProductsQuery(OrderStatus.PAID);
        List<OrderInfo.PaidProduct> paidProducts = orderRepository.findPaidProducts(queryCommand);
        return OrderInfo.PaidProducts.of(paidProducts);
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