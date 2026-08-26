package com.github.gokid96.e_commerce.rank.domain;

import com.github.gokid96.e_commerce.common.key.KeyGeneratable;
import com.github.gokid96.e_commerce.common.key.KeyType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RankKey implements KeyGeneratable {

    private static final String DAYS = "days";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final RankType rankType;
    private final String suffix;

    private RankKey(RankType rankType, String suffix) {
        this.rankType = rankType;
        this.suffix = suffix;
    }

    public static RankKey ofDate(RankType rankType, LocalDate rankDate) {
        return new RankKey(rankType, rankDate.format(FORMATTER));
    }

    public static RankKey ofDays(RankType rankType, int days) {
        return new RankKey(rankType, days + DAYS);
    }

    @Override
    public KeyType type() {
        return KeyType.RANK;
    }

    @Override
    public List<String> namespaces() {
        return List.of(rankType.name().toLowerCase(), suffix);
    }
}