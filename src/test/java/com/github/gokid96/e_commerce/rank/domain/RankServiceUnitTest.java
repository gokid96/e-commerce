package com.github.gokid96.e_commerce.rank.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RankServiceUnitTest {

    @InjectMocks
    private RankService rankService;

    @Mock
    private RankRepository rankRepository;

    @DisplayName("판매 랭크를 생성한다.")
    @Test
    void createSellRank() {
        // given
        RankCommand.CreateList command = RankCommand.CreateList.of(List.of(
                RankCommand.Create.of(1L, 1L, LocalDate.of(2026, 06, 16)),
                RankCommand.Create.of(2L, 2L, LocalDate.of(2026, 06, 16)),
                RankCommand.Create.of(3L, 3L, LocalDate.of(2026, 06, 16))
        ));
        given(rankRepository.save(any())).willReturn(Rank.createSell(1L, LocalDate.of(2026, 06, 16), 1L));

        // when
        rankService.createSellRank(command);

        // then
        verify(rankRepository, times(3)).save(any());

    }

    @DisplayName("인기 판매 랭크를 조회한다.")
    @Test
    void getPopularSellRank() {
        // given
        given(rankRepository.findPopularSellRanks(any())).willReturn(List.of(
                RankInfo.PopularProduct.of(1L, 120L),
                RankInfo.PopularProduct.of(2L, 95L),
                RankInfo.PopularProduct.of(3L, 87L),
                RankInfo.PopularProduct.of(4L, 76L),
                RankInfo.PopularProduct.of(5L, 65L)
        ));
        RankCommand.PopularSellRank command = RankCommand.PopularSellRank.of(5, 3, LocalDate.of(2026, 6, 30));
        // when
        RankInfo.PopularProducts result = rankService.getPopularSellRank(command);

        // then
        assertThat(result.getProducts()).hasSize(5)
                .extracting(RankInfo.PopularProduct::getProductId)
                .containsExactly(1L, 2L, 3L, 4L, 5L);
    }
}
