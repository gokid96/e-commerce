package com.github.gokid96.e_commerce.payment.infrastructure;

import com.github.gokid96.e_commerce.common.client.api.balance.BalanceApiClient;
import com.github.gokid96.e_commerce.common.client.api.balance.BalanceRequest;
import com.github.gokid96.e_commerce.common.client.api.coupon.CouponApiClient;
import com.github.gokid96.e_commerce.common.client.api.coupon.CouponRequest;
import com.github.gokid96.e_commerce.common.client.api.order.OrderApiClient;
import com.github.gokid96.e_commerce.common.client.api.order.OrderResponse;
import com.github.gokid96.e_commerce.payment.domain.PaymentClient;
import com.github.gokid96.e_commerce.payment.domain.PaymentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCoreClient implements PaymentClient {

    private final OrderApiClient orderApiClient;
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

    @Override
    public PaymentInfo.Order getOrder(Long orderId) {
        OrderResponse.Order order = orderApiClient.getOrder(orderId).getData();
        return PaymentInfo.Order.of(
                order.getOrderId(),
                order.getUserId(),
                order.getUserCouponId(),
                order.getTotalPrice());
    }

    @Override
    public void refundBalance(Long userId, long amount) {
        balanceApiClient.refundBalance(userId, BalanceRequest.Refund.of(amount));
    }

    @Override
    public void cancelCoupon(Long userCouponId) {
        couponApiClient.cancelCoupon(userCouponId);
    }
}