package com.github.gokid96.e_commerce.product.interfaces;

import com.github.gokid96.e_commerce.product.domain.product.Product;
import com.github.gokid96.e_commerce.product.domain.product.ProductRepository;
import com.github.gokid96.e_commerce.product.domain.product.ProductSellingStatus;
import com.github.gokid96.e_commerce.support.E2EControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProductControllerE2ETest extends E2EControllerTestSupport {

    @Autowired private ProductRepository productRepository;

    @DisplayName("상품 목록을 가져온다.")
    @Test
    void getProducts() {
        Product product1 = productRepository.save(Product.create("상품1", 100_000L, ProductSellingStatus.SELLING));
        Product product2 = productRepository.save(Product.create("상품2", 200_000L, ProductSellingStatus.SELLING));

        client.get()
                .uri("/api/v1/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data.products[0].id").isEqualTo(product1.getId().intValue())
                .jsonPath("$.data.products[0].name").isEqualTo(product1.getName())
                .jsonPath("$.data.products[0].price").isEqualTo(100_000)
                .jsonPath("$.data.products[1].id").isEqualTo(product2.getId().intValue())
                .jsonPath("$.data.products[1].price").isEqualTo(200_000);
    }
}
