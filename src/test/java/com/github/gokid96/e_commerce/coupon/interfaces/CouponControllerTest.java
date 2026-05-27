package com.github.gokid96.e_commerce.coupon.controller;

import com.github.gokid96.e_commerce.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CouponControllerTest extends ControllerTestSupport {

    @DisplayName("보유 쿠폰 목록을 조회한다.")
    @Test
    void getCoupons() throws Exception {
        // when & then
        mockMvc.perform(
                get("/api/v1/users/{userId}/coupons", 1L)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].name").value("쿠폰명"))
            .andExpect(jsonPath("$.data[0].discountRate").value(0.1));
    }

    @DisplayName("쿠폰을 발급한다.")
    @Test
    void issueCoupon() throws Exception {
        // given
        String requestBody = """
                {
                  "couponId": 1
                }
                """;

        // when & then
        mockMvc.perform(
                post("/api/v1/users/{userId}/coupons/issue", 1L)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("쿠폰 발급 시, 쿠폰 ID는 필수이다.")
    @Test
    void issueCouponWithoutCouponId() throws Exception {
        // given
        String requestBody = "{}";

        // when & then
        mockMvc.perform(
                post("/api/v1/users/{userId}/coupons/issue", 1L)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("쿠폰 ID는 필수입니다."));
    }
}