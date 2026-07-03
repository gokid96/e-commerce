package com.github.gokid96.e_commerce.rank.interfaces;

import com.github.gokid96.e_commerce.rank.application.RankResult;
import com.github.gokid96.e_commerce.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RankControllerTest extends ControllerTestSupport {

    @DisplayName("인기 상품 Top5 목록을 조회한다.")
    @Test
    void getPopularProducts() throws Exception {
        given(rankFacade.getPopularProducts(any()))
                .willReturn(RankResult.PopularProducts.of(List.of(
                        RankResult.PopularProduct.of(1L, "상품명", 30000L)
                )));

        mockMvc.perform(get("/api/v1/products/ranks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.products[0].id").value(1))
                .andExpect(jsonPath("$.data.products[0].name").value("상품명"))
                .andExpect(jsonPath("$.data.products[0].price").value(30000));
    }
}
