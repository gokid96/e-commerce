package com.github.gokid96.e_commerce.docs;

import com.github.gokid96.e_commerce.rank.application.RankFacade;
import com.github.gokid96.e_commerce.rank.application.RankResult;
import com.github.gokid96.e_commerce.rank.interfaces.RankController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RankControllerDocsTest extends RestDocsSupport {

    private final RankFacade rankFacade = Mockito.mock(RankFacade.class);

    @Override
    protected Object initController() {
        return new RankController(rankFacade);
    }

    @DisplayName("인기 상품 Top5 조회 API")
    @Test
    void getPopularProducts() throws Exception {
        given(rankFacade.getPopularProducts(any()))
                .willReturn(RankResult.PopularProducts.of(List.of(
                        RankResult.PopularProduct.of(1L, "상품명", 30000L)
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
