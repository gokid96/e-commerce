package com.github.gokid96.e_commerce.common.client.api.product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Products {
        private List<Long> productIds;

        @Builder
        private Products(List<Long> productIds) {
            this.productIds = productIds;
        }

        public static Products of(List<Long> productIds) {
            return Products.builder().productIds(productIds).build();
        }
    }
}
