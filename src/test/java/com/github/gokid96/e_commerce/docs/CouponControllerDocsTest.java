package com.github.gokid96.e_commerce.docs;

import com.github.gokid96.e_commerce.coupon.controller.CouponController;
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

class CouponControllerDocsTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new CouponController();
    }

    @DisplayName("보유 쿠폰 목록 조회 API")
    @Test
    void getCoupons() throws Exception {
        mockMvc.perform(
                get("/api/v1/users/{userId}/coupons", 1L)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("coupon-list",
                pathParameters(
                    parameterWithName("userId").description("사용자 ID")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data[].id").description("쿠폰 ID"),
                    fieldWithPath("data[].name").description("쿠폰명"),
                    fieldWithPath("data[].discountRate").description("할인율"),
                    fieldWithPath("data[].expiredAt").description("만료일시")
                )
            ));
    }

    @DisplayName("쿠폰 발급 API")
    @Test
    void issueCoupon() throws Exception {
        String requestBody = """
                {
                  "couponId": 1
                }
                """;

        mockMvc.perform(
                post("/api/v1/users/{userId}/coupons/issue", 1L)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("coupon-issue",
                pathParameters(
                    parameterWithName("userId").description("사용자 ID")
                ),
                requestFields(
                    fieldWithPath("couponId").description("발급받을 쿠폰 ID")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지")
                )
            ));
    }
}