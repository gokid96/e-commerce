package com.github.gokid96.e_commerce.product.domain;

import com.github.gokid96.e_commerce.product.domain.product.Product;
import com.github.gokid96.e_commerce.product.domain.product.ProductSellingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class ProductTest {


    @DisplayName("상품 생성 시 이름은 필수이다.")
    @Test
    void createWithBlankName(){
        assertThatThrownBy(()-> Product.create(" ",1000L, ProductSellingStatus.SELLING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 이름은 필수입니다.");
    }

    @DisplayName("상품 생성시 가격은 0보다 커야 한다.")
    @Test
    void createWithNotPositivePrice(){
        assertThatThrownBy(()-> Product.create("상품명",0L, ProductSellingStatus.SELLING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 가격은 0보다 커야 합니다.");
    }

    @DisplayName("상품 생성 시 판매 상태는 필수이다.")
    @Test
    void createWithNullSellStatus() {
        assertThatThrownBy(() -> Product.create("상품명", 1000L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 판매 상태는 필수입니다.");

    }

    @DisplayName("정상적으로 상품을 생성한다.")
    @Test
    void create(){
        // when
        Product product = Product.create("상품명",1000L, ProductSellingStatus.SELLING);

        // then
        assertThat(product.getName()).isEqualTo("상품명");
        assertThat(product.getPrice()).isEqualTo(1000L);
        assertThat(product.getSellStatus()).isEqualTo(ProductSellingStatus.SELLING);
    }

    @DisplayName("판매 중 상태면 판매 가능하다.")
    @Test
    void cannotSelling_false(){
        // given
        Product product = Product.create("상품명",1000L, ProductSellingStatus.SELLING);

        // when & then
        assertThat(product.cannotSelling()).isFalse();
    }

    @DisplayName("판매 보류/중지 상태면 판매할 수 없다.")
    @Test
    void cannotSelling_true(){
        // given
        Product hold = Product.create("상품명",1000L, ProductSellingStatus.HOLD);
        Product stop = Product.create("상품명",1000L, ProductSellingStatus.STOP_SELLING);

        // when & then
        assertThat(hold.cannotSelling()).isTrue();
        assertThat(stop.cannotSelling()).isTrue();
    }



}
