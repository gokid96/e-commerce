package com.github.gokid96.e_commerce.docs;

import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
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

    private final ProductService productService = Mockito.mock(ProductService.class);

    @Override
    protected Object initController() {
        return new ProductController(productService);
    }

    @DisplayName("상품 목록 조회 API")
    @Test
    void getProducts() throws Exception {
        // given
        ProductInfo.Product product = ProductInfo.Product.builder()
                .productId(1L)
                .productName("상품명")
                .productPrice(30000L)
                .stock(100)
                .build();
        given(productService.getSellingProducts())
                .willReturn(ProductInfo.Products.of(List.of(product)));

        // when & then
        mockMvc.perform(get("/api/v1/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("product-list",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.products[].id").description("상품 ID"),
                                fieldWithPath("data.products[].name").description("상품명"),
                                fieldWithPath("data.products[].price").description("가격"),
                                fieldWithPath("data.products[].stock").description("재고 수량")
                        )
                ));
    }
}