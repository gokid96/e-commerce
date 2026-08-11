package com.github.gokid96.e_commerce.common.client.api.product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Deduct {
        private List<Product> products;

        @Builder
        private Deduct(List<Product> products) {
            this.products = products;
        }

        public static Deduct of(List<Product> products) {
            return Deduct.builder().products(products).build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Restore {
        private List<Product> products;

        @Builder
        private Restore(List<Product> products) {
            this.products = products;
        }

        public static Restore of(List<Product> products) {
            return Restore.builder().products(products).build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Product {
        private Long productId;
        private Integer quantity;

        @Builder
        private Product(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static Product of(Long productId, Integer quantity) {
            return Product.builder().productId(productId).quantity(quantity).build();
        }
    }
}
