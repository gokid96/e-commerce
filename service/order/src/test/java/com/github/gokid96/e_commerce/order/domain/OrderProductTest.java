package com.github.gokid96.e_commerce.order.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderProductTest {

    @DisplayName("주문 상품 가격은 단가 x 수량이다.")
    @Test
    void getPrice(){
        // given
        OrderProduct orderProduct = OrderProduct.create(1L,"상품1",1_000L,3  );

        // when
        long price = orderProduct.getPrice();

        // then
        assertThat(price).isEqualTo(3_000L);
    }




}
