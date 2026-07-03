package com.github.gokid96.e_commerce.rank.interfaces.dto;

import com.github.gokid96.e_commerce.rank.application.RankResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankResponse {

    @Getter
    public static class PopularProducts {
        private final List<PopularProduct> products;

        private PopularProducts(List<PopularProduct> products) {
            this.products = products;
        }

        public static PopularProducts of(RankResult.PopularProducts result) {
            List<PopularProduct> products = result.getProducts().stream()
                    .map(PopularProduct::of)
                    .toList();
            return new PopularProducts(products);
        }
    }

    @Getter
    public static class PopularProduct {
        private final Long id;
        private final String name;
        private final long price;

        @Builder
        private PopularProduct(Long id, String name, long price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public static PopularProduct of(RankResult.PopularProduct product) {
            return PopularProduct.builder()
                    .id(product.getProductId())
                    .name(product.getProductName())
                    .price(product.getProductPrice())
                    .build();
        }
    }
}
