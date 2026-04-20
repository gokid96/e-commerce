package com.github.gokid96.e_commerce.order.controller;

import com.github.gokid96.e_commerce.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends ControllerTestSupport {

    @DisplayName("주문을 생성한다.")
    @Test
    void createOrder() throws Exception {
        // given
        String requestBody = """
                {
                  "userId": 1,
                  "couponId": 2,
                  "products": [
                    { "id": 3, "quantity": 2 }
                  ]
                }
                """;

        // when & then
        mockMvc.perform(
                post("/api/v1/orders")
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("주문 생성 시, 쿠폰 ID는 선택 사항이다.")
    @Test
    void createOrderWithoutCouponId() throws Exception {
        // given
        String requestBody = """
                {
                  "userId": 1,
                  "products": [
                    { "id": 3, "quantity": 2 }
                  ]
                }
                """;

        // when & then
        mockMvc.perform(
                post("/api/v1/orders")
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @DisplayName("주문 생성 시, 사용자 ID는 필수이다.")
    @Test
    void createOrderWithoutUserId() throws Exception {
        // given
        String requestBody = """
                {
                  "products": [
                    { "id": 3, "quantity": 2 }
                  ]
                }
                """;

        // when & then
        mockMvc.perform(
                post("/api/v1/orders")
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("사용자 ID는 필수입니다."));
    }

    @DisplayName("주문 생성 시, 상품 목록은 1개 이상이어야 한다.")
    @Test
    void createOrderWithEmptyProducts() throws Exception {
        // given
        String requestBody = """
                {
                  "userId": 1,
                  "products": []
                }
                """;

        // when & then
        mockMvc.perform(
                post("/api/v1/orders")
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("상품 목록은 1개 이상이어야 합니다."));
    }

    @DisplayName("주문 생성 시, 상품 구매 수량은 양수여야 한다.")
    @Test
    void createOrderWithZeroQuantity() throws Exception {
        // given
        String requestBody = """
                {
                  "userId": 1,
                  "products": [
                    { "id": 3, "quantity": 0 }
                  ]
                }
                """;

        // when & then
        mockMvc.perform(
                post("/api/v1/orders")
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("상품 구매 수량은 양수여야 합니다."));
    }
}