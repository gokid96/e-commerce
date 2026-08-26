package com.github.gokid96.e_commerce.balance.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BalanceTest {

    @DisplayName("잔액 생성 시 금액은 0보다 커야한다.")
    @Test
    void createWithNotPositiveAmount(){
        assertThatThrownBy(() -> Balance.create(1L,0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("충전 금액은 양수여야 합니다.");
    }

    @DisplayName("충전 금액은 0보다 커야 한다.")
    @Test
    void chargeWithNotPositiveAmount(){
        // given
        Balance balance = Balance.create(1L,1_000_000L);

        // when & then
        assertThatThrownBy(() -> balance.charge(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("충전 금액은 양수여야 합니다.");
    }

    @DisplayName("잔액을 충전한다.")
    @Test
    void charge(){
        // given
        Balance balance = Balance.create(1L,1_000_000L);

        // when
        balance.charge(1_000_000L);

        // then
        assertThat(balance.getAmount()).isEqualTo(2_000_000L);
    }

    @DisplayName("사용 금액은 0보다 커야 한다.")
    @Test
    void useWithNotPositiveAmount(){
        // given
        Balance balance = Balance.create(1L,1_000_000L);

        // when & then
        assertThatThrownBy(() ->balance.use(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용 금액은 양수여야 합니다.");

    }

    @DisplayName("잔고가 부족할 경우 차감할 수 없다.")
    @Test
    void useCannotInsufficientAmount(){
        // given
        Balance balance = Balance.create(1L,1_000_000L);

        // when & then
        assertThatThrownBy(() ->balance.use(1_000_001L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔액이 부족합니다.");

    }

    @DisplayName("잔고를 차감한다.")
    @Test
    void use(){
        //given
        Balance balance = Balance.create(1L,1_000_000L);

        //when
        balance.use(1_000_000L);

        assertThat(balance.getAmount()).isZero();
    }

    @DisplayName("최대 잔액을 초과해 충전할 수 없다.")
    @Test
    void charge_exceedMax(){
        // given
        Balance balance = Balance.create(1L,9_000_000L);
        // when & then
        assertThatThrownBy(()-> balance.charge(2_000_000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 잔액(1,000만원)을 초과할 수 없습니다.");
    }
}
