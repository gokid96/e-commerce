package com.github.gokid96.e_commerce.product.controller;

import com.github.gokid96.e_commerce.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends ControllerTestSupport {

    @DisplayName("상품 목록을 조회한다.")
    @Test
    void getProducts() throws Exception {
        // when & then
        mockMvc.perform(
                get("/api/v1/products")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].name").value("상품명"))
            .andExpect(jsonPath("$.data[0].price").value(30000))
            .andExpect(jsonPath("$.data[0].stock").value(100));
    }

    @DisplayName("인기 상품 Top5를 조회한다.")
    @Test
    void getRanks() throws Exception {
        // when & then
        mockMvc.perform(
                get("/api/v1/products/ranks")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].name").value("상품명"))
            .andExpect(jsonPath("$.data[0].price").value(30000))
            .andExpect(jsonPath("$.data[0].saleCount").value(150));
    }
}