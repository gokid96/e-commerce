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
    private OrderEventPublisher orderEventPublisher;

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
        verify(orderEventPublisher, times(1)).paid(any(OrderEvent.Paid.class));
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

}
