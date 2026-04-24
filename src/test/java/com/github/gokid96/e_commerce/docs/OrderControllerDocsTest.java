package com.github.gokid96.e_commerce.docs;

import com.github.gokid96.e_commerce.balance.controller.BalanceController;
import com.github.gokid96.e_commerce.balance.dto.request.BalanceChargeRequest;
import com.github.gokid96.e_commerce.order.controller.OrderController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerDocsTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new OrderController();
    }

    @DisplayName("주문 생성 API")
    @Test
    void createOrder() throws Exception {
        String requestBody = """
                {
                  "userId": 1,
                  "couponId": 2,
                  "products": [
                    { "id": 3, "quantity": 2 }
                  ]
                }
                """;

        mockMvc.perform(
                post("/api/v1/orders")
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("order-create",
                requestFields(
                    fieldWithPath("userId").description("사용자 ID"),
                    fieldWithPath("couponId").description("사용할 쿠폰 ID (선택)").optional(),
                    fieldWithPath("products[].id").description("상품 ID"),
                    fieldWithPath("products[].quantity").description("구매 수량")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지")
                )
            ));
    }

    static class BalanceControllerDocsTest extends RestDocsSupport {

        @Override
        protected Object initController() {
            return new BalanceController();
        }

        @DisplayName("잔액 조회 API")
        @Test
        void getBalance() throws Exception {
            // when then
            mockMvc.perform(
                    get("/api/v1/users/{userId}/balance", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("balance-get",
                    pathParameters(
                        parameterWithName("userId").description("사용자 ID")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data.amount").description("잔액")
                    )
                ));
        }

        @DisplayName("잔액 충전 API")
        @Test
        void chargeBalance() throws Exception {
            // given
            BalanceChargeRequest request = BalanceChargeRequest.of(10000L);

            // when then
            mockMvc.perform(
                    post("/api/v1/users/{userId}/balance/charge", 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("balance-charge",
                    pathParameters(
                        parameterWithName("userId").description("사용자 ID")
                    ),
                    requestFields(
                        fieldWithPath("amount").description("충전 금액")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지")
                    )
                ));
        }
    }
}