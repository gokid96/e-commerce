package com.github.gokid96.e_commerce.rank.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankResult {

    @Getter
    public static class PopularProducts {
        private final List<PopularProduct> products;

        @Builder
        @JsonCreator
        private PopularProducts(@JsonProperty("products") List<PopularProduct> products) {
            this.products = products;
        }

        public static PopularProducts of(List<PopularProduct> products) {
            return PopularProducts.builder().products(products).build();
        }
    }

    @Getter
    public static class PopularProduct {
        private final Long productId;
        private final String productName;
        private final long productPrice;

        @Builder
        @JsonCreator
        private PopularProduct(@JsonProperty("productId") Long productId,
                               @JsonProperty("productName") String productName,
                               @JsonProperty("productPrice") long productPrice) {
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
        }

        public static PopularProduct of(Long productId, String productName, long productPrice) {
            return PopularProduct.builder()
                    .productId(productId)
                    .productName(productName)
                    .productPrice(productPrice)
                    .build();
        }
    }
}
