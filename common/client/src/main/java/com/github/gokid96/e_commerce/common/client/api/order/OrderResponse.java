package com.github.gokid96.e_commerce.common.client.api.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponse {

    @Getter
    @NoArgsConstructor
    public static class Order {
        private Long orderId;
        private Long userId;
        private Long userCouponId;
        private long totalPrice;
        private long discountPrice;
    }
}