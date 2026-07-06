package com.github.gokid96.e_commerce.rank.domain;

import java.util.List;

public interface RankRepository {

    Rank save(Rank rank);

    List<RankInfo.PopularProduct> findPopularSellRanks(RankCommand.Query command);
}
