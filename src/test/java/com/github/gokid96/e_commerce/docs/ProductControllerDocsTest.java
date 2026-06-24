package com.github.gokid96.e_commerce.docs;

import com.github.gokid96.e_commerce.product.application.ProductFacade;
import com.github.gokid96.e_commerce.product.application.ProductResult;
import com.github.gokid96.e_commerce.product.interfaces.ProductController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerDocsTest extends RestDocsSupport {

    private final ProductFacade productFacade = Mockito.mock(ProductFacade.class);

    @Override
    protected Object initController() {
        return new ProductController(productFacade);
    }

    @DisplayName("상품 목록 조회 API")
    @Test
    void getProducts() throws Exception {
        given(productFacade.getProducts())
                .willReturn(ProductResult.Products.of(List.of(
                        ProductResult.Product.of(1L, "상품명", 30000L)
                )));

        mockMvc.perform(get("/api/v1/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("product-list",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.products[].id").description("상품 ID"),
                                fieldWithPath("data.products[].name").description("상품명"),
                                fieldWithPath("data.products[].price").description("가격")
                        )
                ));
    }

    @DisplayName("인기 상품 조회 API")
    @Test
    void getPopularProducts() throws Exception {
        given(productFacade.getPopularProducts())
                .willReturn(ProductResult.Products.of(List.of(
                        ProductResult.Product.of(1L, "상품명", 30000L)
                )));

        mockMvc.perform(get("/api/v1/products/ranks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("product-ranks",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.products[].id").description("상품 ID"),
                                fieldWithPath("data.products[].name").description("상품명"),
                                fieldWithPath("data.products[].price").description("가격")
                        )
                ));
    }
}
