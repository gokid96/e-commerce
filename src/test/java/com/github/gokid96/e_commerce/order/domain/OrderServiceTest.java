package com.github.gokid96.e_commerce.order.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderExternalClient orderExternalClient;

    @InjectMocks
    private OrderService orderService;

    @DisplayName("주문을 생성하면 총 금액을 계산하고 저장한다.")
    @Test
    void createOrder() {
        // given
        List<OrderCommand.OrderProduct> products = List.of(
                OrderCommand.OrderProduct.of(1L, "상품1", 1_000L, 1),
                OrderCommand.OrderProduct.of(2L, "상품2", 2_000L, 2)
        );
        OrderCommand.Create command = OrderCommand.Create.of(1L, null, 0.0, products);

        // when
        OrderInfo.Order info = orderService.createOrder(command);

        // then
        assertThat(info.getTotalPrice()).isEqualTo(5_000L);
        assertThat(info.getDiscountPrice()).isZero();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @DisplayName("주문을 결제 완료 상태로 변경한다.")
    @Test
    void paidOrder() {
        // given
        Order order = Order.create(1L, null, 0.0, List.of(
                OrderProduct.create(1L, "상품1", 1_000L, 1)
        ));
        given(orderRepository.findById(1L))
                .willReturn(Optional.of(order));
        // when
        orderService.paidOrder(1L);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderExternalClient, times(1)).sendOrderMessage(order);
    }

    @DisplayName("주문이 존재하지 않으면 결제 완료 시 예외가 발생한다.")
    @Test
    void paidOrder_notFound() {
        // given
        given(orderRepository.findById(1L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.paidOrder(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문이 존재하지 않습니다.");
    }

    @DisplayName("결제 완료된 주문 상품들을 판매 수량 기준 내림차순으로 정렬해 반환한다.")
    @Test
    void getTopPaidProducts() {
        // given
        List<OrderProduct> orderProducts = List.of(
                OrderProduct.create(1L, "상품명", 2_000L, 2),
                OrderProduct.create(2L, "상품명", 3_000L, 3),
                OrderProduct.create(1L, "상품명", 2_000L, 4),
                OrderProduct.create(3L, "상품명", 2_000L, 3),
                OrderProduct.create(4L, "상품명", 2_000L, 2),
                OrderProduct.create(5L, "상품명", 2_000L, 1)
        );
        given(orderRepository.findOrderIdsIn(any()))
                .willReturn(orderProducts);

        OrderCommand.TopOrders command = OrderCommand.TopOrders.of(List.of(1L, 2L), 5);

        // when
        OrderInfo.TopPaidProducts topPaidProducts = orderService.getTopPaidProducts(command);

        // then
        assertThat(topPaidProducts.getProductIds()).hasSize(5)
                .containsExactly(1L, 2L, 3L, 4L, 5L);

    }

    @DisplayName("결제 완료된 주문이 없으면 빈 인기상품 목록을 반환한다.")
    @Test
    void getTopPaidProducts_empty() {
        // given
        given(orderRepository.findOrderIdsIn(any()))
                .willReturn(List.of());

        OrderCommand.TopOrders command = OrderCommand.TopOrders.of(List.of(1L, 2L), 5);

        // when
        OrderInfo.TopPaidProducts topPaidProducts = orderService.getTopPaidProducts(command);

        // then
        assertThat(topPaidProducts.getProductIds()).isEmpty();
    }
}
