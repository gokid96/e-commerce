package com.github.gokid96.e_commerce.coupon.infrastructure;

import com.github.gokid96.e_commerce.coupon.domain.CouponCommand;
import com.github.gokid96.e_commerce.coupon.domain.CouponInfo;
import com.github.gokid96.e_commerce.coupon.domain.UserCouponKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserCouponRedisRepository {

    private final RedisTemplate<String, Long> redisTemplate;

    public boolean save(CouponCommand.PublishRequest command) {
        String key = UserCouponKey.of(command.getCouponId()).generate();
        long score = command.getIssuedAt().toEpochSecond(ZoneOffset.UTC);

        return Boolean.TRUE.equals(
                redisTemplate.opsForZSet().addIfAbsent(key, command.getUserId(), score));
    }

    public List<CouponInfo.Candidates> findPublishCandidates(CouponCommand.Candidates command) {
        String key = UserCouponKey.of(command.getCouponId()).generate();
        Set<TypedTuple<Long>> tuples =
                redisTemplate.opsForZSet().rangeWithScores(key, command.getStart(), command.getEnd() - 1);

        return Optional.ofNullable(tuples)
                .map(this::toCandidates)
                .orElse(new ArrayList<>());
    }

    private List<CouponInfo.Candidates> toCandidates(Set<TypedTuple<Long>> tuples) {
        return tuples.stream()
                .map(this::toCandidate)
                .toList();
    }

    private CouponInfo.Candidates toCandidate(TypedTuple<Long> tuple) {
        Long userId = tuple.getValue();
        LocalDateTime issuedAt = Optional.ofNullable(tuple.getScore())
                .map(Double::longValue)
                .map(s -> LocalDateTime.ofEpochSecond(s, 0, ZoneOffset.UTC))
                .orElse(LocalDateTime.now());

        return CouponInfo.Candidates.of(userId, issuedAt);
    }
}