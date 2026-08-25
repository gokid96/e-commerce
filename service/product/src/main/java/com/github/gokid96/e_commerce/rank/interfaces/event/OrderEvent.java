package com.github.gokid96.e_commerce.rank.interfaces.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class OrderEvent {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Completed {
        private List<OrderProduct> orderProducts;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderProduct {
        private Long productId;
        private int quantity;
    }
}
