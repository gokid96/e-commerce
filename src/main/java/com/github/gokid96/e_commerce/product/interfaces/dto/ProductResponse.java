package com.github.gokid96.e_commerce.product.interfaces.dto;

import com.github.gokid96.e_commerce.product.application.ProductResult;
import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
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
        private final int stock;

        @Builder
        private Product(Long id, String name, long price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        public static Product of(ProductResult.Product info) {
            return Product.builder()
                    .id(info.getProductId())
                    .name(info.getProductName())
                    .price(info.getProductPrice())
                    .stock(info.getStock())
                    .build();
        }

    }
}
