package com.github.gokid96.e_commerce.rank.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RankType {

    SELL("판매"),
    ;

    private final String description;
}
