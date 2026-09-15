package com.github.gokid96.e_commerce.product.interfaces;

import com.github.gokid96.e_commerce.product.application.ProductResult;
import com.github.gokid96.e_commerce.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends ControllerTestSupport {

    @DisplayName("판매 중인 상품 목록을 조회한다.")
    @Test
    void getProducts() throws Exception {
        given(productFacade.getProducts())
                .willReturn(ProductResult.Products.of(List.of(
                        ProductResult.Product.of(1L, "상품명", 30000L)
                )));

        mockMvc.perform(get("/api/v1/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.products[0].id").value(1))
                .andExpect(jsonPath("$.data.products[0].name").value("상품명"))
                .andExpect(jsonPath("$.data.products[0].price").value(30000))
                .andDo(document("product-list",
                        relaxedResponseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.products[].id").description("상품 ID"),
                                fieldWithPath("data.products[].name").description("상품명"),
                                fieldWithPath("data.products[].price").description("상품 가격")
                        )));
    }
}
