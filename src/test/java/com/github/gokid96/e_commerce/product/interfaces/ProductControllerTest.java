package com.github.gokid96.e_commerce.product.interfaces;

import com.github.gokid96.e_commerce.product.application.ProductResult;
import com.github.gokid96.e_commerce.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends ControllerTestSupport {

    @DisplayName("판매 중인 상품 목록을 조회한다.")
    @Test
    void getProducts() throws Exception {
        given(productFacade.getProducts())
                .willReturn(ProductResult.Products.of(List.of(
                        ProductResult.Product.of(1L, "상품명", 30000L, 100)
                )));

        mockMvc.perform(get("/api/v1/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.products[0].id").value(1))
                .andExpect(jsonPath("$.data.products[0].name").value("상품명"))
                .andExpect(jsonPath("$.data.products[0].price").value(30000))
                .andExpect(jsonPath("$.data.products[0].stock").value(100));
    }

    @DisplayName("인기 상품 목록을 조회한다.")
    @Test
    void getPopularProducts() throws Exception {
        given(productFacade.getPopularProducts())
                .willReturn(ProductResult.Products.of(List.of(
                        ProductResult.Product.of(1L, "상품명", 30000L, 100)
                )));

        mockMvc.perform(get("/api/v1/products/ranks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.products[0].id").value(1))
                .andExpect(jsonPath("$.data.products[0].name").value("상품명"))
                .andExpect(jsonPath("$.data.products[0].price").value(30000))
                .andExpect(jsonPath("$.data.products[0].stock").value(100));
    }
}
