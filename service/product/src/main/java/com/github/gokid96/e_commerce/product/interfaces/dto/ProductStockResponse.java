package com.github.gokid96.e_commerce.product.interfaces.dto;

import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductStockResponse {

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
        private final int stock;

        @Builder
        private Product(Long productId, String productName, long productPrice, int stock) {
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
            this.stock = stock;
        }

        public static Product of(ProductInfo.Product info, int stock) {
            return Product.builder()
                    .productId(info.getProductId())
                    .productName(info.getProductName())
                    .productPrice(info.getProductPrice())
                    .stock(stock)
                    .build();
        }
    }
}