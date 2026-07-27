package com.github.gokid96.e_commerce.order.domain;

import com.github.gokid96.e_commerce.support.IntegrationTestSupport;
import com.github.gokid96.e_commerce.support.database.RedisKeyCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
@RecordApplicationEvents
class OrderServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ApplicationEvents events;

    @Autowired
    private RedisKeyCleaner redisKeyCleaner;

    @MockitoBean
    private OrderClient orderClient;

    @MockitoSpyBean
    private OrderEventPublisher orderEventPublisher;

    @AfterEach
    void tearDown() {
        redisKeyCleaner.clean();
    }

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

    @DisplayName("주문을 생성 시, 이벤트를 발행한다.")
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

        verify(orderEventPublisher, times(1)).created(any(OrderEvent.Created.class));
        assertThat(events.stream(OrderEvent.Created.class).count()).isEqualTo(1);
    }

    @DisplayName("주문을 결제완료 처리한다.")
    @Test
    void completedOrder() {
        Order order = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(1L, "상품1", 10_000L, 2),
                OrderProduct.create(2L, "상품2", 20_000L, 3)));
        orderRepository.save(order);

        OrderInfo.Completed completed = orderService.completedOrder(order.getId());

        assertThat(completed.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문을 취소한다.")
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
    }

    @DisplayName("주문 프로세스 갱신 시, 아직 대기중인 프로세스가 있으면 대기한다.")
    @Test
    void updateProcessPending() {
        OrderCommand.Process command = OrderCommand.Process.ofStockDeducted(1L, OrderProcessStatus.SUCCESS);

        orderService.updateProcess(command);

        List<OrderProcess> process = orderRepository.getProcess(OrderKey.of(command.getOrderId()));
        assertThat(process).hasSize(3)
                .extracting(OrderProcess::getTask, OrderProcess::getStatus)
                .containsExactlyInAnyOrder(
                        tuple(OrderProcessTask.STOCK_DEDUCTED, OrderProcessStatus.SUCCESS),
                        tuple(OrderProcessTask.BALANCE_USED, OrderProcessStatus.PENDING),
                        tuple(OrderProcessTask.COUPON_USED, OrderProcessStatus.PENDING));
    }

    @DisplayName("주문 프로세스 갱신 시, 실패한 프로세스가 있으면 실패 이벤트를 발행한다.")
    @Test
    void updateProcessFailed() {
        Order order = Order.create(1L, null, 0.1, List.of(
                OrderProduct.create(1L, "상품1", 10_000L, 2),
                OrderProduct.create(2L, "상품2", 20_000L, 3)));
        orderRepository.save(order);

        orderRepository.updateProcess(OrderCommand.Process.ofCouponUsed(order.getId(), OrderProcessStatus.FAILED));
        orderRepository.updateProcess(OrderCommand.Process.ofUsedBalance(order.getId(), OrderProcessStatus.FAILED));

        OrderCommand.Process command = OrderCommand.Process.ofStockDeducted(order.getId(), OrderProcessStatus.FAILED);

        orderService.updateProcess(command);

        List<OrderProcess> process = orderRepository.getProcess(OrderKey.of(command.getOrderId()));
        assertThat(process).hasSize(3)
                .extracting(OrderProcess::getTask, OrderProcess::getStatus)
                .containsExactlyInAnyOrder(
                        tuple(OrderProcessTask.STOCK_DEDUCTED, OrderProcessStatus.FAILED),
                        tuple(OrderProcessTask.BALANCE_USED, OrderProcessStatus.FAILED),
                        tuple(OrderProcessTask.COUPON_USED, OrderProcessStatus.FAILED));

        verify(orderEventPublisher, atLeastOnce()).failed(any(OrderEvent.Failed.class));
        assertThat(events.stream(OrderEvent.Failed.class).count()).isEqualTo(1);
    }

    @DisplayName("주문 프로세스 갱신 시, 모든 프로세스가 성공하면 결제 대기 이벤트를 발행한다.")
    @Test
    void updateProcessCompleted() {
        Order order = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(1L, "상품1", 10_000L, 2),
                OrderProduct.create(2L, "상품2", 20_000L, 3)));
        orderRepository.save(order);

        orderRepository.updateProcess(OrderCommand.Process.ofCouponUsed(order.getId(), OrderProcessStatus.SUCCESS));
        orderRepository.updateProcess(OrderCommand.Process.ofUsedBalance(order.getId(), OrderProcessStatus.SUCCESS));

        OrderCommand.Process command = OrderCommand.Process.ofStockDeducted(order.getId(), OrderProcessStatus.SUCCESS);

        orderService.updateProcess(command);

        List<OrderProcess> process = orderRepository.getProcess(OrderKey.of(command.getOrderId()));
        assertThat(process).hasSize(3)
                .extracting(OrderProcess::getTask, OrderProcess::getStatus)
                .containsExactlyInAnyOrder(
                        tuple(OrderProcessTask.STOCK_DEDUCTED, OrderProcessStatus.SUCCESS),
                        tuple(OrderProcessTask.BALANCE_USED, OrderProcessStatus.SUCCESS),
                        tuple(OrderProcessTask.COUPON_USED, OrderProcessStatus.SUCCESS));

        verify(orderEventPublisher, times(1)).paymentWaited(any(OrderEvent.PaymentWaited.class));
        assertThat(events.stream(OrderEvent.PaymentWaited.class).count()).isEqualTo(1);
    }
}