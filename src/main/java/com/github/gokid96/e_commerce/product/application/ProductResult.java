package com.github.gokid96.e_commerce.product.application;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResult {

    @Getter
    public static class Products {
        private final List<Product> products;

        @Builder
        private Products(List<Product> products) {
            this.products = products;
        }

        public static Products of(List<Product> products) {
            return Products.builder().products(products).build();
        }
    }

    @Getter
    public static class Product {
        private final Long productId;
        private final String productName;
        private final long productPrice;


        @Builder
        private Product(Long productId, String productName, long productPrice) {
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
        }

        public static Product of(Long productId, String productName, long productPrice) {
            return Product.builder()
                    .productId(productId)
                    .productName(productName)
                    .productPrice(productPrice)
                    .build();
        }
    }
}