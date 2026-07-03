package com.github.gokid96.e_commerce.common.key;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeyType {

    RANK("랭크"),
    USER_COUPON("사용자 쿠폰"),
    ;

    private final String description;

    public String getKey() {
        return this.name().toLowerCase();
    }


}
