package com.github.gokid96.e_commerce.rank.domain;

import com.github.gokid96.e_commerce.support.IntegrationTestSupport;
import com.github.gokid96.e_commerce.support.database.RedisKeyCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private RedisKeyCleaner redisKeyCleaner;

    @BeforeEach
    void setUp() {
        redisKeyCleaner.clean();
    }

    @AfterEach
    void tearDown() {
        redisKeyCleaner.clean();
    }

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
        // given: 기준일 6/30, days=3 → 6/27~6/30 포함, 6/26은 범위 밖
        List<Rank> ranks = List.of(
                Rank.createSell(1L, LocalDate.of(2026, 6, 26), 10L),
                Rank.createSell(2L, LocalDate.of(2026, 6, 26), 34L),
                Rank.createSell(3L, LocalDate.of(2026, 6, 28), 32L),
                Rank.createSell(4L, LocalDate.of(2026, 6, 29), 41L),
                Rank.createSell(5L, LocalDate.of(2026, 6, 30), 33L),
                Rank.createSell(6L, LocalDate.of(2026, 6, 30), 51L),
                Rank.createSell(7L, LocalDate.of(2026, 6, 27), 89L),
                Rank.createSell(8L, LocalDate.of(2026, 6, 28), 60L)
        );
        ranks.forEach(rankRepository::save);

        RankCommand.PopularSellRank command =
                RankCommand.PopularSellRank.of(5, 3, LocalDate.of(2026, 6, 30));

        // when
        RankInfo.PopularProducts result = rankService.getPopularSellRank(command);

        // then (6/26 제외, 점수 desc top5)
        assertThat(result.getProducts()).hasSize(5)
                .extracting(RankInfo.PopularProduct::getProductId)
                .containsExactly(7L, 8L, 6L, 4L, 5L);
    }

    @DisplayName("일별 랭크를 DB로 영속하면 DB에 저장되고 Redis 키는 삭제된다.")
    @Test
    void persistDailyRank() {
        // given
        LocalDate date = LocalDate.of(2026, 5, 27);
        rankRepository.save(Rank.createSell(1L, date, 10L));
        rankRepository.save(Rank.createSell(2L, date, 20L));

        // when
        rankService.persistDailyRank(date);

        // then
        assertThat(rankRepository.findBy(RankType.SELL, date)).hasSize(2)
                .extracting(Rank::getProductId)
                .containsExactlyInAnyOrder(1L, 2L);
        assertThat(rankRepository.findDailyRank(RankKey.ofDate(RankType.SELL, date))).isEmpty();
    }
}