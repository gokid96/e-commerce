package com.github.gokid96.e_commerce.product.domain.product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductInfo {

    @Getter
    public static class Products {
        private final List<Product> products;

        private Products(List<Product> products) {
            this.products = products;
        }

        public static Products of(List<Product> products) {
            return new Products(products);
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

        public static Product of(
                com.github.gokid96.e_commerce.product.domain.product.Product product) {
            return Product.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productPrice(product.getPrice())
                    .build();
        }
    }

    @Getter
    public static class OrderProducts {
        private final List<OrderProduct> products;

        @Builder
        private OrderProducts(List<OrderProduct> products) {
            this.products = products;
        }

        public static OrderProducts of(List<OrderProduct> products) {
            return OrderProducts.builder()
                    .products(products)
                    .build();
        }
    }

    @Getter
    public static class OrderProduct {
        private final Long productId;
        private final String productName;
        private final long productPrice;
        private final int quantity;

        @Builder
        private OrderProduct(Long productId, String productName, long productPrice, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long productId, String productName, long productPrice, int quantity) {
            return OrderProduct.builder()
                    .productId(productId)
                    .productName(productName)
                    .productPrice(productPrice)
                    .quantity(quantity)
                    .build();
        }
    }


}
