package com.github.gokid96.e_commerce.common.lock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LockType {

    COUPON("쿠폰"),
    ;
    private final String description;

    public String createKey(String key) {
        return this.name().toLowerCase() + ":" + key;
    }
}
