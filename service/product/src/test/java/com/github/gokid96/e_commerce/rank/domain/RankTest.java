package com.github.gokid96.e_commerce.rank.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RankTest {

    @DisplayName("판매 랭크 생성 시, 상품은 필수다.")
    @Test
    void createSellWithoutProductId() {
        assertThatThrownBy(() -> Rank.createSell(null, LocalDate.of(2025, 4, 23), 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품이 존재하지 않습니다.");
    }

    @DisplayName("판매 랭크 생성 시, 랭크 날짜는 필수다.")
    @Test
    void createSellWithoutRankDate(){
        assertThatThrownBy(()-> Rank.createSell(1L, null, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("날짜가 존재하지 않습니다.");
    }
}


