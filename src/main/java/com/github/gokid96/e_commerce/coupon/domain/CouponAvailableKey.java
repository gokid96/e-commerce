package com.github.gokid96.e_commerce.coupon.domain;

import com.github.gokid96.e_commerce.common.key.KeyGeneratable;
import com.github.gokid96.e_commerce.common.key.KeyType;

import java.util.List;

public class CouponAvailableKey implements KeyGeneratable {

    private final Long couponId;

    private CouponAvailableKey(Long couponId) {
        this.couponId = couponId;
    }

    public static CouponAvailableKey of(Long couponId) {
        return new CouponAvailableKey(couponId);
    }

    @Override
    public KeyType type() {
        return KeyType.COUPON_AVAILABLE;
    }

    @Override
    public List<String> namespaces() {
        return List.of(couponId.toString());
    }
}
