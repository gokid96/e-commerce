package com.github.gokid96.e_commerce.order.interfaces.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PaymentEvent {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Paid {
        private Long paymentId;
        private Long orderId;
        private Long userId;
        private long totalPrice;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayFailed {
        private Long orderId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Canceled {
        private Long orderId;
    }
}
