package com.github.gokid96.e_commerce.order.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderTest {

    @DisplayName("할인이 없는 주문을 생성한다.")
    @Test
    void createWithoutDiscount() {
        //given
        List<OrderProduct> orderProducts = List.of(
                OrderProduct.create(1L, "상품1", 1_000L, 1),
                OrderProduct.create(2L, "상품2", 2_000L, 2)
                );
        // when
        Order order = Order.create(1L,null,0.0,orderProducts);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(5_000L);
        assertThat(order.getDiscountPrice()).isZero();
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CREATED);

    }

    @DisplayName("할인이 있는 주문을 생성한다.")
    @Test
    void createWithDiscount() {
        // given
        List<OrderProduct> orderProducts = List.of(
                OrderProduct.create(1L,"상품1",1_000L,1),
                OrderProduct.create(2L,"상품2",2_000L,2)
        );
        // when
        Order order = Order.create(1L,10L,0.1,orderProducts);

        // then
        long total  = 5_000L;
        long discount  = (long) (total * 0.1);
        assertThat(order.getDiscountPrice()).isEqualTo(discount);
        assertThat(order.getTotalPrice()).isEqualTo(total - discount);
    }

    @DisplayName("주문을 결제 완료 상태로 변경한다.")
    @Test
    void paid() {
        // given
        Order order = Order.create(1L,null,0.0, List.of(
                OrderProduct.create(1L,"상품1",1_000L,1)
        ));

        // when
        order.paid();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }
}