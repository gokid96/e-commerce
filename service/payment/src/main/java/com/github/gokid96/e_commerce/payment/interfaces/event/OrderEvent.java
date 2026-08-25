package com.github.gokid96.e_commerce.payment.interfaces.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OrderEvent {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Created {
        private Long orderId;
        private Long userId;
        private Long userCouponId;
        private long totalPrice;
    }
}
