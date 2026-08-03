package com.github.gokid96.e_commerce.coupon.domain;

public interface CouponEventPublisher {

    void publishRequested(CouponEvent.PublishRequested event);

    void published(CouponEvent.Published event);
}
