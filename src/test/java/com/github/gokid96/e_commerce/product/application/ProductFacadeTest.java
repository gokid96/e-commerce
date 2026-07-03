package com.github.gokid96.e_commerce.product.application;

import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductFacadeTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductFacade productFacade;

    @DisplayName("판매 중인 상품 목록을 조회한다.")
    @Test
    void getProducts() {
        given(productService.getSellingProducts())
                .willReturn(ProductInfo.Products.of(List.of(
                        ProductInfo.Product.builder().productId(1L).productName("상품A").productPrice(1000L).build()
                )));

        ProductResult.Products result = productFacade.getProducts();

        assertThat(result.getProducts()).hasSize(1)
                .extracting("productId", "productName", "productPrice")
                .containsExactly(tuple(1L, "상품A", 1000L));
    }
}
