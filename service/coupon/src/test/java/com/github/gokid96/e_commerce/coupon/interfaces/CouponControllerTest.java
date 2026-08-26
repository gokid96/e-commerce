package com.github.gokid96.e_commerce.coupon.interfaces;

import com.github.gokid96.e_commerce.coupon.application.CouponResult;
import com.github.gokid96.e_commerce.coupon.domain.UserCouponUsedStatus;
import com.github.gokid96.e_commerce.coupon.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CouponControllerTest extends ControllerTestSupport {

    @DisplayName("쿠폰을 발급한다.")
    @Test
    void issueCoupon() throws Exception {
        // given
        String content = "{\"couponId\": 5}";

        // when & then
        mockMvc.perform(post("/api/v1/users/{userId}/coupons/issue", 1L)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("쿠폰 발급 시 쿠폰 ID는 필수이다.")
    @Test
    void issueCouponWithoutCouponId() throws Exception {
        // given
        String content = "{}";

        // when & then
        mockMvc.perform(post("/api/v1/users/{userId}/coupons/issue", 1L)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("쿠폰 ID는 필수입니다."));
    }

    @DisplayName("쿠폰을 사용한다.")
    @Test
    void useCoupon() throws Exception {
        // given
        String content = "{\"userCouponId\": 100}";

        // when & then
        mockMvc.perform(post("/api/v1/users/{userId}/coupons/use", 1L)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("쿠폰 사용 시 사용자 쿠폰 ID는 필수이다.")
    @Test
    void useCouponWithoutUserCouponId() throws Exception {
        // given
        String content = "{}";

        // when & then
        mockMvc.perform(post("/api/v1/users/{userId}/coupons/use", 1L)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("사용자 쿠폰 ID는 필수입니다."));
    }

    @DisplayName("사용자가 보유한 쿠폰 목록을 조회한다.")
    @Test
    void getUserCoupons() throws Exception {
        // given
        CouponResult.UserCoupon result = CouponResult.UserCoupon.builder()
                .userCouponId(100L)
                .couponId(5L)
                .couponName("신규 가입 할인")
                .discountRate(0.1)
                .usedStatus(UserCouponUsedStatus.UNUSED)
                .build();
        given(couponFacade.getUserCoupons(1L)).willReturn(List.of(result));

        // when & then
        mockMvc.perform(get("/api/v1/users/{userId}/coupons", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].couponName").value("신규 가입 할인"))
                .andExpect(jsonPath("$.data[0].discountRate").value(0.1))                .andExpect(jsonPath("$.data[0].usedStatus").value("UNUSED"));
    }
}