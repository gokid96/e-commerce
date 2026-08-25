package com.github.gokid96.e_commerce.rank.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RankKeyTest {

    @DisplayName("날짜로 일별 키를 생성한다.")
    @Test
    void ofDate() {
        RankKey key = RankKey.ofDate(RankType.SELL, LocalDate.of(2026, 7, 3));

        assertThat(key.generate()).isEqualTo("rank:sell:20260703");
    }

    @DisplayName("일수로 합산 키를 생성한다.")
    @Test
    void ofDays() {
        RankKey key = RankKey.ofDays(RankType.SELL, 3);

        assertThat(key.generate()).isEqualTo("rank:sell:3days");
    }
}