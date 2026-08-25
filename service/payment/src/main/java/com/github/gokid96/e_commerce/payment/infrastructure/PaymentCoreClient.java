package com.github.gokid96.e_commerce.payment.infrastructure;

import com.github.gokid96.e_commerce.common.client.api.balance.BalanceApiClient;
import com.github.gokid96.e_commerce.common.client.api.balance.BalanceRequest;
import com.github.gokid96.e_commerce.common.client.api.coupon.CouponApiClient;
import com.github.gokid96.e_commerce.common.client.api.coupon.CouponRequest;
import com.github.gokid96.e_commerce.payment.domain.PaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCoreClient implements PaymentClient {

    private final BalanceApiClient balanceApiClient;
    private final CouponApiClient couponApiClient;

    @Override
    public void useBalance(Long userId, long amount) {
        balanceApiClient.useBalance(userId, BalanceRequest.Use.of(amount));
    }

    @Override
    public void useCoupon(Long userCouponId) {
        couponApiClient.useUserCoupon(CouponRequest.Use.of(userCouponId));
    }
}
