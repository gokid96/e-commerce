package com.github.gokid96.e_commerce.rank.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RankKeysTest {

    @DisplayName("기준일 포함 최근 N+1일치 일별 키들을 생성한다.")
    @Test
    void ofDaysWithDate() {
        RankKeys keys = RankKeys.ofDaysWithDate(RankType.SELL, 3, LocalDate.of(2026, 7, 3));

        assertThat(keys.getFirstKey()).isEqualTo("rank:sell:20260703");
        assertThat(keys.getOtherKeys())
                .containsExactly("rank:sell:20260702", "rank:sell:20260701", "rank:sell:20260630");
    }
}