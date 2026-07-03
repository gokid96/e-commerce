package com.github.gokid96.e_commerce.rank.interfaces;

import com.github.gokid96.e_commerce.product.domain.product.Product;
import com.github.gokid96.e_commerce.product.domain.product.ProductRepository;
import com.github.gokid96.e_commerce.product.domain.product.ProductSellingStatus;
import com.github.gokid96.e_commerce.rank.domain.Rank;
import com.github.gokid96.e_commerce.rank.domain.RankRepository;
import com.github.gokid96.e_commerce.support.E2EControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

class RankControllerE2ETest extends E2EControllerTestSupport {

    @Autowired private ProductRepository productRepository;
    @Autowired private RankRepository rankRepository;

    @DisplayName("인기 상품 Top5 목록을 가져온다.")
    @Test
    void getPopularProducts() {
        Product product1 = productRepository.save(Product.create("상품1", 100_000L, ProductSellingStatus.SELLING));
        Product product2 = productRepository.save(Product.create("상품2", 200_000L, ProductSellingStatus.SELLING));
        Product product3 = productRepository.save(Product.create("상품3", 300_000L, ProductSellingStatus.SELLING));
        Product product4 = productRepository.save(Product.create("상품4", 400_000L, ProductSellingStatus.SELLING));
        Product product5 = productRepository.save(Product.create("상품5", 500_000L, ProductSellingStatus.SELLING));

        rankRepository.save(Rank.createSell(product1.getId(), LocalDate.now().minusDays(3), 10));
        rankRepository.save(Rank.createSell(product2.getId(), LocalDate.now().minusDays(2), 20));
        rankRepository.save(Rank.createSell(product3.getId(), LocalDate.now().minusDays(1), 30));
        rankRepository.save(Rank.createSell(product4.getId(), LocalDate.now().minusDays(1), 40));
        rankRepository.save(Rank.createSell(product5.getId(), LocalDate.now().minusDays(1), 50));

        client.get()
                .uri("/api/v1/products/ranks")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data.products[0].id").isEqualTo(product5.getId().intValue())
                .jsonPath("$.data.products[0].price").isEqualTo(500_000)
                .jsonPath("$.data.products[1].id").isEqualTo(product4.getId().intValue())
                .jsonPath("$.data.products[2].id").isEqualTo(product3.getId().intValue())
                .jsonPath("$.data.products[3].id").isEqualTo(product2.getId().intValue())
                .jsonPath("$.data.products[4].id").isEqualTo(product1.getId().intValue());
    }
}
