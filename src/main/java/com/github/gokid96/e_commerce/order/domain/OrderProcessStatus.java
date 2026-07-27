package com.github.gokid96.e_commerce.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderProcessStatus {

    SUCCESS("성공"),
    FAILED("실패"),
    PENDING("대기"),
    ;

    private final String description;
}