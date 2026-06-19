package com.github.gokid96.e_commerce.order.domain;

import com.github.gokid96.e_commerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
class OrderServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("결제 완료된 상품을 요청한 날짜(전날) 기준으로 집계 조회한다.")
    @Test
    void getPaidProducts() {
        // given
        Order order1 = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(1L, "상품1", 10_000L, 2),
                OrderProduct.create(2L, "상품2", 20_000L, 3)));
        Order order2 = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(1L, "상품1", 10_000L, 2),
                OrderProduct.create(3L, "상품3", 30_000L, 4)));
        Order order3 = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(2L, "상품2", 20_000L, 3),
                OrderProduct.create(3L, "상품3", 30_000L, 4)));

        List.of(order1, order2, order3).forEach(o -> {
            o.paid(LocalDateTime.of(2026, 6, 22, 12, 0, 0));
            orderRepository.save(o);
        });

        OrderCommand.DateQuery command = OrderCommand.DateQuery.of(LocalDate.of(2026, 6, 23));

        // when
        OrderInfo.PaidProducts result = orderService.getPaidProducts(command);

        // then
        assertThat(result.getProducts()).hasSize(3)
                .extracting("productId", "quantity")
                .containsExactlyInAnyOrder(tuple(1L, 4), tuple(2L, 6), tuple(3L, 8));
    }
}