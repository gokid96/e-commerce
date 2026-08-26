package com.github.gokid96.e_commerce.common.client.api.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponse {

    @Getter
    @NoArgsConstructor
    public static class Products {
        private List<Product> products;
    }

    @Getter
    @NoArgsConstructor
    public static class Product {
        private Long productId;
        private String productName;
        private long productPrice;
        private int stock;
    }
}
