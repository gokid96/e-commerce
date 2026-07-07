package com.github.gokid96.e_commerce.rank.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankInfo {

    @Getter
    public static class PopularProduct {
        private final Long productId;
        private final Long totalScore;

        private PopularProduct(Long productId, Long totalScore) {
            this.productId = productId;
            this.totalScore = totalScore;
        }

        public static PopularProduct of(Long productId, Long totalScore) {
            return new PopularProduct(productId, totalScore);
        }
    }

    @Getter
    public static class PopularProducts {
        private final List<PopularProduct> products;

        @Builder
        private PopularProducts(List<PopularProduct> products) {
            this.products = products;
        }

        public static PopularProducts of(List<PopularProduct> products) {
            return PopularProducts.builder().products(products).build();
        }

        public List<Long> getProductIds() {
            return products.stream()
                    .map(PopularProduct::getProductId)
                    .toList();
        }
    }
}