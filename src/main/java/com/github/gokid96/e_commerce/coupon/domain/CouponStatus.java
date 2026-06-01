package com.github.gokid96.e_commerce.coupon.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum CouponStatus {

    REGISTERED("등록"),
    PUBLISHABLE("발급 가능"),
    CANCELED("취소");

    private final String description;

    private static final List<CouponStatus> CANNOT_PUBLISHABLE_STATUSES = List.of(REGISTERED, CANCELED);

    public boolean cannotPublishable() {
        return CANNOT_PUBLISHABLE_STATUSES.contains(this);
    }
}