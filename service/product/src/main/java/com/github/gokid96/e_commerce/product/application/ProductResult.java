package com.github.gokid96.e_commerce.product.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

        // @JsonCreator + @JsonProperty: 캐시(Redis JSON) 역직렬화 시 이 생성자를 쓰게 한다.
        @Builder
        @JsonCreator
        private Products(@JsonProperty("products") List<Product> products) {
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
        @JsonCreator
        private Product(@JsonProperty("productId") Long productId,
                        @JsonProperty("productName") String productName,
                        @JsonProperty("productPrice") long productPrice) {
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
