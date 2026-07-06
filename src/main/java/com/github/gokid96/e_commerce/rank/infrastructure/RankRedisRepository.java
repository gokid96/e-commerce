package com.github.gokid96.e_commerce.rank.infrastructure;

import com.github.gokid96.e_commerce.rank.domain.Rank;
import com.github.gokid96.e_commerce.rank.domain.RankCommand;
import com.github.gokid96.e_commerce.rank.domain.RankInfo;
import com.github.gokid96.e_commerce.rank.domain.RankKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RankRedisRepository {

    private final RedisTemplate<String, Long> redisTemplate;

    public Rank save(Rank rank) {
        String key = rank.toKey().generate();
        redisTemplate.opsForZSet().incrementScore(key, rank.getProductId(), rank.getScore());
        return rank;
    }

    public List<RankInfo.PopularProduct> findPopularSellRanks(RankCommand.Query command) {
        String targetKey = command.getTarget().generate();
        RankKeys sources = command.getSources();

        redisTemplate.opsForZSet().unionAndStore(sources.getFirstKey(), sources.getOtherKeys(), targetKey);
        Set<TypedTuple<Long>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(targetKey, 0, command.getTop() - 1);

        return Optional.ofNullable(tuples)
                .map(this::toPopularProducts)
                .orElse(new ArrayList<>());
    }

    private List<RankInfo.PopularProduct> toPopularProducts(Set<TypedTuple<Long>> tuples) {
        return tuples.stream()
                .map(this::toPopularProduct)
                .toList();
    }

    private RankInfo.PopularProduct toPopularProduct(TypedTuple<Long> tuple) {
        Long productId = tuple.getValue();
        long score = Optional.ofNullable(tuple.getScore()).map(Double::longValue).orElse(0L);
        return RankInfo.PopularProduct.of(productId, score);
    }
}