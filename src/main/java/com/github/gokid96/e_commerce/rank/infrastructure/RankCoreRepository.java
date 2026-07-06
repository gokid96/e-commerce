package com.github.gokid96.e_commerce.rank.infrastructure;

import com.github.gokid96.e_commerce.rank.domain.Rank;
import com.github.gokid96.e_commerce.rank.domain.RankCommand;
import com.github.gokid96.e_commerce.rank.domain.RankInfo;
import com.github.gokid96.e_commerce.rank.domain.RankKey;
import com.github.gokid96.e_commerce.rank.domain.RankRepository;
import com.github.gokid96.e_commerce.rank.domain.RankType;
import com.github.gokid96.e_commerce.rank.infrastructure.jpa.RankJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RankCoreRepository implements RankRepository {

    private final RankJpaRepository rankJpaRepository;
    private final RankQueryDslRepository rankQueryDslRepository;
    private final RankRedisRepository rankRedisRepository;
    private final RankJdbcTemplateRepository rankJdbcTemplateRepository;   // 필드 추가

    @Override
    public Rank save(Rank rank) {
        return rankRedisRepository.save(rank);
    }

    @Override
    public List<RankInfo.PopularProduct> findPopularSellRanks(RankCommand.Query command) {
        return rankRedisRepository.findPopularSellRanks(command);
    }

    @Override
    public List<RankInfo.PopularProduct> findDailyRank(RankKey key) {
        return rankRedisRepository.findDailyRank(key);
    }

    @Override
    public List<Rank> findBy(RankType rankType, LocalDate date) {
        return rankJpaRepository.findByRankTypeAndRankDate(rankType, date);
    }

    @Override
    public void saveAll(List<Rank> ranks) {
        rankJdbcTemplateRepository.batchInsert(ranks);
    }

    @Override
    public boolean delete(RankKey key) {
        return rankRedisRepository.delete(key);
    }
}