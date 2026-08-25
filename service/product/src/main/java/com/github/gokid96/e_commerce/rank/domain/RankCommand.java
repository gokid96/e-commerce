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
        private final int days;
        private final LocalDate date;

        @Builder
        private PopularSellRank(int top, int days, LocalDate date) {
            this.top = top;
            this.days = days;
            this.date = date;
        }

        public static PopularSellRank of(int top, int days, LocalDate date) {
            return PopularSellRank.builder().top(top).days(days).date(date).build();
        }
    }

    @Getter
    public static class Query {
        private final int top;
        private final RankKey target;
        private final RankKeys sources;

        @Builder
        private Query(int top, RankKey target, RankKeys sources) {
            this.top = top;
            this.target = target;
            this.sources = sources;
        }

        public static Query of(int top, RankKey target, RankKeys sources) {
            return Query.builder().top(top).target(target).sources(sources).build();
        }
    }
}
