package com.github.gokid96.e_commerce.product.domain.product;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCommand {

    @Getter
    public static class OrderProducts {
        private final List<OrderProduct> products;

        @Builder
        private OrderProducts(List<OrderProduct> products) {
            this.products = products;
        }

        public static OrderProducts of(List<OrderProduct> products) {
            return OrderProducts.builder().products(products).build();
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
                    .quantity(quantity).build();
        }
    }

    @Getter
    public static class Products {
        private final List<Long> productIds;

        @Builder
        private Products(List<Long> productIds) {
            this.productIds = productIds;
        }

        public static Products of(List<Long> productIds) {
            return Products.builder().productIds(productIds).build();
        }
    }

    @Getter
    public static class Query {

        private final Long pageSize;
        private final Long cursor;
        private final ProductSellingStatus status;

        private Query(Long pageSize, Long cursor, ProductSellingStatus status) {
            this.pageSize = pageSize;
            this.cursor = cursor;
            this.status = status;
        }

        public static Query of(Long pageSize, Long cursor) {
            return new Query(pageSize, cursor, ProductSellingStatus.SELLING);
        }
    }
}
