package com.github.gokid96.e_commerce.docs;

import com.github.gokid96.e_commerce.balance.application.BalanceFacade;
import com.github.gokid96.e_commerce.balance.application.BalanceResult;
import com.github.gokid96.e_commerce.balance.interfaces.BalanceController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BalanceControllerDocsTest extends RestDocsSupport {

    private final BalanceFacade balanceFacade = Mockito.mock(BalanceFacade.class);

    @Override
    protected Object initController() {
        return new BalanceController(balanceFacade);
    }

    @DisplayName("잔액 조회 API")
    @Test
    void getBalance() throws Exception {
        // given
        BalanceResult.Balance result = BalanceResult.Balance.builder()
                .amount(1_000_000L)
                .build();
        given(balanceFacade.getBalance(any(Long.class))).willReturn(result);

        // when & then
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
        String content = "{\"amount\": 10000}";

        // when & then
        mockMvc.perform(
                        post("/api/v1/users/{userId}/balance/charge", 1L)
                                .content(content)
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