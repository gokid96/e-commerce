package com.github.gokid96.e_commerce.order.domain;

import com.github.gokid96.e_commerce.order.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
class OrderServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private OrderClient orderClient;

    @MockitoSpyBean
    private OrderEventPublisher orderEventPublisher;

    @DisplayName("주문 생성 시, 사용자는 존재해야 한다.")
    @Test
    void createOrderWithInvalidUser() {
        OrderCommand.Create command = OrderCommand.Create.of(1L, 1L, List.of(
                OrderCommand.OrderProduct.of(1L, 2)));

        when(orderClient.getUser(anyLong()))
                .thenThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        assertThatThrownBy(() -> orderService.createOrder(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("주문 생성 시, 유효한 상품만 존재해야 한다.")
    @Test
    void createOrderWithInvalidProduct() {
        OrderCommand.Create command = OrderCommand.Create.of(1L, 1L, List.of(
                OrderCommand.OrderProduct.of(1L, 2)));

        when(orderClient.getProducts(any()))
                .thenThrow(new IllegalArgumentException("상품이 존재하지 않습니다."));

        assertThatThrownBy(() -> orderService.createOrder(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품이 존재하지 않습니다.");
    }

    @DisplayName("주문 생성 시, 쿠폰 ID가 있으면 사용가능한 쿠폰이어야 한다.")
    @Test
    void createOrderWithInvalidCoupon() {
        OrderCommand.Create command = OrderCommand.Create.of(1L, 1L, List.of(
                OrderCommand.OrderProduct.of(1L, 2)));

        when(orderClient.getUsableCoupon(anyLong()))
                .thenThrow(new IllegalStateException("사용할 수 없는 쿠폰입니다."));

        assertThatThrownBy(() -> orderService.createOrder(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("사용할 수 없는 쿠폰입니다.");
    }

    @DisplayName("주문을 생성한다.")
    @Test
    void createOrder() {
        OrderCommand.Create command = OrderCommand.Create.of(1L, 1L, List.of(
                OrderCommand.OrderProduct.of(1L, 2)));

        when(orderClient.getUser(anyLong()))
                .thenReturn(OrderInfo.User.of(1L, "사용자"));
        when(orderClient.getProducts(any()))
                .thenReturn(List.of(OrderInfo.Product.of(1L, "상품명", 2_000L, 2)));
        when(orderClient.getUsableCoupon(anyLong()))
                .thenReturn(OrderInfo.Coupon.of(1L, 2L, "쿠폰명", 0.1, LocalDateTime.of(2025, 1, 1, 0, 0)));

        OrderInfo.Order order = orderService.createOrder(command);

        assertThat(order.getTotalPrice()).isEqualTo(3_600L);
        assertThat(order.getDiscountPrice()).isEqualTo(400L);
    }

    @DisplayName("주문 생성 시, 재고를 차감하고 생성 이벤트를 발행한다.")
    @Test
    void createOrderWithPublishEvent() {
        OrderCommand.Create command = OrderCommand.Create.of(1L, 1L, List.of(
                OrderCommand.OrderProduct.of(1L, 2)));

        when(orderClient.getUser(anyLong()))
                .thenReturn(OrderInfo.User.of(1L, "사용자"));
        when(orderClient.getProducts(any()))
                .thenReturn(List.of(OrderInfo.Product.of(1L, "상품명", 2_000L, 2)));
        when(orderClient.getUsableCoupon(anyLong()))
                .thenReturn(OrderInfo.Coupon.of(1L, 2L, "쿠폰명", 0.1, LocalDateTime.of(2025, 1, 1, 0, 0)));

        orderService.createOrder(command);

        verify(orderClient, times(1)).deductStock(any());
        verify(orderEventPublisher, times(1)).created(any(OrderEvent.Created.class));
    }

    @DisplayName("주문을 결제완료 처리하고 완료 이벤트를 발행한다.")
    @Test
    void completedOrder() {
        Order order = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(1L, "상품1", 10_000L, 2),
                OrderProduct.create(2L, "상품2", 20_000L, 3)));
        orderRepository.save(order);

        orderService.completedOrder(order.getId());

        Order result = orderRepository.findById(order.getId())
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
        verify(orderEventPublisher, times(1)).completed(any(OrderEvent.Completed.class));
    }

    @DisplayName("주문을 취소하고 재고를 복구한다.")
    @Test
    void cancelOrder() {
        Order order = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(1L, "상품1", 10_000L, 2),
                OrderProduct.create(2L, "상품2", 20_000L, 3)));
        orderRepository.save(order);

        orderService.cancelOrder(order.getId());

        Order result = orderRepository.findById(order.getId())
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(orderClient, times(1)).restoreStock(any());
    }
}
