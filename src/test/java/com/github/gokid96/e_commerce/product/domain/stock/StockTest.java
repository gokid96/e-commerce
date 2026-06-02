package com.github.gokid96.e_commerce.product.domain.stock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StockTest {

    @DisplayName("재고 생성 시 수량은 0 이상이어야 한다.")
    @Test
    void createWithNegativeQuantity(){
        assertThatThrownBy(()-> Stock.create(1L,-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 수량은 0 이상이어야 합니다.");
    }

    @DisplayName("수량이 0이어도 재고를 생성할 수 있다.")
    @Test
    void createWithZeroQuantity(){
        // when
        Stock stock = Stock.create(1L,0);

        //then
        assertThat(stock.getProductId()).isEqualTo(1L);
        assertThat(stock.getQuantity()).isZero();
    }

    @DisplayName("정상적으로 재고를 생성한다.")
    @Test
    void creat(){
        // when
        Stock stock = Stock.create(5L,100);

        // then
        assertThat(stock.getProductId()).isEqualTo(5L);
        assertThat(stock.getQuantity()).isEqualTo(100);
    }


}
