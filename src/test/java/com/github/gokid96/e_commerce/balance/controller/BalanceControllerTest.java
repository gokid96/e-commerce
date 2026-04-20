package com.github.gokid96.e_commerce.balance.controller;

import com.github.gokid96.e_commerce.balance.dto.request.BalanceChargeRequest;
import com.github.gokid96.e_commerce.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BalanceControllerTest extends ControllerTestSupport {

    @DisplayName("잔액을 조회한다.")
    @Test
    void getBalance() throws Exception {
        // when & then
        mockMvc.perform(
                        get("/api/v1/users/{userId}/balance", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.amount").value(1000000));
    }

    @DisplayName("잔액 충전 시, 금액은 필수이다.")
    @Test
    void chargeBalanceWithoutAmount() throws Exception {
        // given
        BalanceChargeRequest request = new BalanceChargeRequest();

        // when & then
        mockMvc.perform(
                        post("/api/v1/users/{userId}/balance/charge", 1L)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("충전 금액은 필수입니다."));
    }

    @DisplayName("잔액 충전 시, 금액은 양수여야 한다.")
    @ParameterizedTest
    @ValueSource(longs = {-1000L, 0L})
    void chargeBalanceWithNegativeOrZeroAmount(long amount) throws Exception {
        // given
        BalanceChargeRequest request = BalanceChargeRequest.of(amount);

        // when & then
        mockMvc.perform(
                        post("/api/v1/users/{userId}/balance/charge", 1L)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("충전 금액은 양수여야 합니다."));
    }

    @DisplayName("잔액을 충전한다.")
    @Test
    void chargeBalance() throws Exception {
        // given
        BalanceChargeRequest request = BalanceChargeRequest.of(10000L);

        // when & then
        mockMvc.perform(
                        post("/api/v1/users/{userId}/balance/charge", 1L)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"));
    }
}