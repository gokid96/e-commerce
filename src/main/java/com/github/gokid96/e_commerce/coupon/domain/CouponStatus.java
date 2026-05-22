package com.github.gokid96.e_commerce.coupon.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponStatus {

    AVAILABLE("발급 가능"),
    UNAVAILABLE("발급 불가");

    private final String description;
}