package com.github.gokid96.e_commerce.coupon.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum UserCouponUsedStatus {

    UNUSED("미사용"),
    USED("사용 완료");

    private final String description;

    private static final List<UserCouponUsedStatus> CANNOT_USABLE_STATUSES = List.of(USED);

    public boolean cannotUsable() {
        return CANNOT_USABLE_STATUSES.contains(this);
    }

    public static List<UserCouponUsedStatus> forUsable() {
        return List.of(UNUSED);
    }
}