package com.github.gokid96.e_commerce.rank.infrastructure;

import com.github.gokid96.e_commerce.rank.domain.RankCommand;
import com.github.gokid96.e_commerce.rank.domain.RankInfo;
import com.github.gokid96.e_commerce.rank.domain.RankType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.github.gokid96.e_commerce.rank.domain.QRank.rank;

@Repository
@RequiredArgsConstructor
public class RankQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<RankInfo.PopularProduct> findPopularSellRanks(RankCommand.PopularSellRank command) {
        return queryFactory.select(
                        Projections.constructor(
                                RankInfo.PopularProduct.class,
                                rank.productId,
                                rank.score.sumAggregate().as("totalScore")
                        ))
                .from(rank)
                .where(rank.rankDate.between(command.getStartDate(), command.getEndDate()))
                .where(
                        rank.rankType.eq(RankType.SELL),
                        rank.rankDate.between(command.getStartDate(), command.getEndDate())
                )
                .groupBy(rank.productId)
                .orderBy(rank.score.sumAggregate().desc())
                .limit(command.getTop())
                .fetch();
    }
}