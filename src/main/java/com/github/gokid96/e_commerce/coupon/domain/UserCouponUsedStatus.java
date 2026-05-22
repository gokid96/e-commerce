package com.github.gokid96.e_commerce.coupon.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserCouponUsedStatus {

    UNUSED("미사용"),
    USED("사용 완료");

    private final String description;

}
