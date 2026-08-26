package com.github.gokid96.e_commerce.product.interfaces.dto;

import com.github.gokid96.e_commerce.product.application.ProductResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponse {

    @Getter
    public static class Products {
        private final List<Product> products;

        private Products(List<Product> products) {
            this.products = products;
        }

        public static Products of(ProductResult.Products result) {
            List<Product> products = result.getProducts().stream()
                    .map(Product::of)
                    .toList();
            return new Products(products);
        }
    }

    @Getter
    public static class Product {
        private final Long id;
        private final String name;
        private final long price;

        @Builder
        private Product(Long id, String name, long price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public static Product of(ProductResult.Product info) {
            return Product.builder()
                    .id(info.getProductId())
                    .name(info.getProductName())
                    .price(info.getProductPrice())
                    .build();
        }

    }
}
