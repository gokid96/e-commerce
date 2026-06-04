package com.github.gokid96.e_commerce.product.domain.stock;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockCommand {

    @Getter
    public static class OrderProducts {
        private final List<OrderProduct> products;

        private OrderProducts(List<OrderProduct> products) {
            this.products = products;
        }

        public static OrderProducts of(List<OrderProduct> products) {
            return new OrderProducts(products);
        }

    }

    @Getter
    public static class OrderProduct {
        private final Long productId;
        private final int quantity;

        @Builder
        private OrderProduct(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long productId, int quantity) {
            return OrderProduct.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .build();
        }

    }

}
