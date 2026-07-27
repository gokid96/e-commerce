package com.github.gokid96.e_commerce.order.domain;

import com.github.gokid96.e_commerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
class OrderEventIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoSpyBean
    private OrderEventPublisher orderEventPublisher;

    @DisplayName("주문을 결제하면 결제 완료 이벤트를 발행한다.")
    @Test
    void publishEventAfterPaidOrder() {
        // given
        Order order = Order.create(1L, null, 0.0, List.of(
                OrderProduct.create(1L, "상품1", 1_000L, 1)));
        orderRepository.save(order);

        // when
        orderService.paidOrder(order.getId());

        // then
        verify(orderEventPublisher, times(1)).paid(any(OrderEvent.Paid.class));
    }

    @DisplayName("주문 결제에 실패하면 이벤트를 발행하지 않는다.")
    @Test
    void notPublishEventWhenPaidFailed() {
        // given
        Long notExistOrderId = -1L;

        // when & then
        assertThatThrownBy(() -> orderService.paidOrder(notExistOrderId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문이 존재하지 않습니다.");

        verify(orderEventPublisher, never()).paid(any(OrderEvent.Paid.class));
    }
}