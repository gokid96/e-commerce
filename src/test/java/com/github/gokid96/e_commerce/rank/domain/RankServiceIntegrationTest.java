package com.github.gokid96.e_commerce.rank.domain;


import com.github.gokid96.e_commerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class RankServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private RankService rankService;

    @Autowired
    private RankRepository rankRepository;

    @DisplayName("판매 랭크를 생성한다.")
    @Test
    void createSellRank() {
        // given
        RankCommand.CreateList command = RankCommand.CreateList.of(List.of(
                RankCommand.Create.of(1L, 1L, LocalDate.of(2026, 6, 23)),
                RankCommand.Create.of(2L, 2L, LocalDate.of(2026, 6, 23)),
                RankCommand.Create.of(3L, 3L, LocalDate.of(2026, 6, 23))
        ));

        // when
        List<Rank> results = rankService.createSellRank(command);

        // then
        assertThat(results).hasSize(3)
                .extracting(Rank::getProductId)
                .containsExactly(1L, 2L, 3L);
    }

    @DisplayName("인기 판매 랭크를 조회한다.")
    @Test
    void getPopularSellRank() {
        // given
        List<Rank> ranks = List.of(
                Rank.createSell(1L, LocalDate.of(2026, 6, 23), 10L),
                Rank.createSell(2L, LocalDate.of(2026, 6, 22), 34L),
                Rank.createSell(3L, LocalDate.of(2026, 6, 23), 32L),
                Rank.createSell(4L, LocalDate.of(2026, 6, 23), 41L),
                Rank.createSell(5L, LocalDate.of(2026, 6, 23), 33L),
                Rank.createSell(6L, LocalDate.of(2026, 6, 23), 51L),
                Rank.createSell(7L, LocalDate.of(2026, 6, 30), 89L),
                Rank.createSell(8L, LocalDate.of(2026, 6, 23), 60L)
        );
        ranks.forEach(rankRepository::save);

        RankCommand.PopularSellRank command = RankCommand.PopularSellRank.of(
                5, LocalDate.of(2026, 6, 23), LocalDate.of(2026, 6, 30));


        // when
        RankInfo.PopularProducts result = rankService.getPopularSellRank(command);


        // then (6.22제외, 점수 desc top5)
        assertThat(result.getProducts()).hasSize(5)
                .extracting(RankInfo.PopularProduct::getProductId)
                .containsExactly(7L, 8L, 6L, 4L, 5L);
    }

}



