package com.github.gokid96.e_commerce.docs;

import com.github.gokid96.e_commerce.product.controller.ProductController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerDocsTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new ProductController();
    }

    @DisplayName("상품 목록 조회 API")
    @Test
    void getProducts() throws Exception {
        mockMvc.perform(
                get("/api/v1/products")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("product-list",
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data[].id").description("상품 ID"),
                    fieldWithPath("data[].name").description("상품명"),
                    fieldWithPath("data[].price").description("가격"),
                    fieldWithPath("data[].stock").description("재고 수량")
                )
            ));
    }

    @DisplayName("인기 상품 Top5 조회 API")
    @Test
    void getRanks() throws Exception {
        mockMvc.perform(
                get("/api/v1/products/ranks")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("product-ranks",
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data[].id").description("상품 ID"),
                    fieldWithPath("data[].name").description("상품명"),
                    fieldWithPath("data[].price").description("가격"),
                    fieldWithPath("data[].saleCount").description("판매량")
                )
            ));
    }
}