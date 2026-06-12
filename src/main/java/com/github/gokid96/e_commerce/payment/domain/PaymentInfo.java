package com.github.gokid96.e_commerce.payment.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentInfo {

    @Getter
    public static class Orders {
        private final List<Long> orderIds;

        @Builder
        private Orders(List<Long> orderIds) {
            this.orderIds = orderIds;
        }

        public static Orders of(List<Long> orderIds) {
            return Orders.builder()
                    .orderIds(orderIds)
                    .build();
        }
    }
}
