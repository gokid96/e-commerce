package com.github.gokid96.e_commerce.order.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

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

        order.paid();
    }

    public OrderInfo.TopPaidProducts getTopPaidProducts(OrderCommand.TopOrders command) {
        List<OrderProduct> orderProducts = orderRepository.findOrderIdsIn(command.getOrderIds());

        Map<Long, Integer> productQuantityMap = groupingProductMap(orderProducts);
        List<Long> sortedProductIds = sortedProducts(productQuantityMap);

        return OrderInfo.TopPaidProducts.of(sortedProductIds);
    }

    private OrderProduct toOrderProduct(OrderCommand.OrderProduct command) {
        return OrderProduct.create(
                command.getProductId(),
                command.getProductName(),
                command.getProductPrice(),
                command.getQuantity()
        );
    }

    private Map<Long, Integer> groupingProductMap(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .collect(groupingBy(
                        OrderProduct::getProductId,
                        Collectors.summingInt(OrderProduct::getQuantity)
                ));
    }

    private static List<Long> sortedProducts(Map<Long, Integer> productQuantityMap) {
        return productQuantityMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }
}