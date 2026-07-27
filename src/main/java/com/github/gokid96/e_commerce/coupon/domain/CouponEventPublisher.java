package com.github.gokid96.e_commerce.coupon.domain;

public interface CouponEventPublisher {

    void used(CouponEvent.Used event);

    void useFailed(CouponEvent.UseFailed event);

    void canceled(CouponEvent.Canceled event);
}