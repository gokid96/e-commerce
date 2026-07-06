package com.github.gokid96.e_commerce.rank.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankService {

    private final RankRepository rankRepository;

    public List<Rank> createSellRank(RankCommand.CreateList command) {
        return command.getRanks().stream()
                .map(this::createSell)
                .map(rankRepository::save)
                .toList();
    }

    public RankInfo.PopularProducts getPopularSellRank(RankCommand.PopularSellRank command) {

        RankKey target = RankKey.ofDays(RankType.SELL, command.getDays());
        RankKeys sources = RankKeys.ofDaysWithDate(RankType.SELL, command.getDays(), command.getDate());

        RankCommand.Query query = RankCommand.Query.of(command.getTop(), target, sources);
        return RankInfo.PopularProducts.of(rankRepository.findPopularSellRanks(query));
    }

    private Rank createSell(RankCommand.Create command) {
        return Rank.createSell(command.getProductId(), command.getRankDate(), command.getScore());
    }
}