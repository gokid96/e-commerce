package com.github.gokid96.e_commerce.payment.domain;

public interface PaymentClient {

    void useBalance(Long userId, long amount);

    void useCoupon(Long userCouponId);
}
