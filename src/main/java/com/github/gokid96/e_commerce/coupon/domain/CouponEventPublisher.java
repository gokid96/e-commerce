package com.github.gokid96.e_commerce.coupon.domain;

public interface CouponEventPublisher {

    void publishRequested(CouponEvent.PublishRequested event);

    void published(CouponEvent.Published event);

    void used(CouponEvent.Used event);

    void useFailed(CouponEvent.UseFailed event);

    void canceled(CouponEvent.Canceled event);
}