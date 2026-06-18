package com.github.gokid96.e_commerce.rank.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankCommand {

    @Getter
    public static class CreateList {
        private final List<Create> ranks;

        @Builder
        private CreateList(List<Create> ranks) {
            this.ranks = ranks;
        }

        public static CreateList of(List<Create> ranks) {
            return CreateList.builder().ranks(ranks).build();
        }
    }

    @Getter
    public static class Create {
        private final Long productId;
        private final long score;
        private final LocalDate rankDate;

        @Builder
        private Create(Long productId, long score, LocalDate rankDate) {
            this.productId = productId;
            this.score = score;
            this.rankDate = rankDate;
        }

        public static Create of(long productId, long score, LocalDate rankDate) {
            return Create.builder().productId(productId).score(score).rankDate(rankDate).build();
        }
    }

    @Getter
    public static class PopularSellRank {
        private final int top;
        private final LocalDate startDate;
        private final LocalDate endDate;

        @Builder
        private PopularSellRank(int top, LocalDate startDate, LocalDate endDate) {
            this.top = top;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public static PopularSellRank of(int top, LocalDate startDate, LocalDate endDate) {
            return PopularSellRank.builder().top(top).startDate(startDate).endDate(endDate).build();
        }
    }

}
