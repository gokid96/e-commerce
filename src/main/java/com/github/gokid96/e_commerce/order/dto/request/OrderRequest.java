package com.github.gokid96.e_commerce.order.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderRequest {
    private Long userId;
    private Long couponId;
    private List<OrderProductRequest> products;

    @Getter
    @NoArgsConstructor
    public static class OrderProductRequest {
        private Long id;
        private int quantity;
    }
}
