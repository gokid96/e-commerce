package com.github.gokid96.e_commerce.docs;

import com.github.gokid96.e_commerce.coupon.application.CouponFacade;
import com.github.gokid96.e_commerce.coupon.application.CouponResult;
import com.github.gokid96.e_commerce.coupon.domain.UserCouponUsedStatus;
import com.github.gokid96.e_commerce.coupon.interfaces.CouponController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import java.util.List;

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

class CouponControllerDocsTest extends RestDocsSupport {

    private final CouponFacade couponFacade = Mockito.mock(CouponFacade.class);

    @Override
    protected Object initController() {
        return new CouponController(couponFacade);
    }

    @DisplayName("쿠폰 발급 API")
    @Test
    void issueCoupon() throws Exception {
        // given
        String content = "{\"couponId\": 5}";

        // when & then
        mockMvc.perform(
                        post("/api/v1/users/{userId}/coupons/issue", 1L)
                                .content(content)
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

    @DisplayName("쿠폰 사용 API")
    @Test
    void useCoupon() throws Exception {
        // given
        String content = "{\"userCouponId\": 100}";

        // when & then
        mockMvc.perform(
                        post("/api/v1/users/{userId}/coupons/use", 1L)
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("coupon-use",
                        pathParameters(
                                parameterWithName("userId").description("사용자 ID")
                        ),
                        requestFields(
                                fieldWithPath("userCouponId").description("사용할 사용자 쿠폰 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @DisplayName("사용자 보유 쿠폰 목록 조회 API")
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
                                fieldWithPath("data[].userCouponId").description("발급된 쿠폰 ID"),
                                fieldWithPath("data[].couponId").description("쿠폰 정책 ID"),
                                fieldWithPath("data[].couponName").description("쿠폰 이름"),
                                fieldWithPath("data[].discountRate").description("할인율"),
                                fieldWithPath("data[].usedStatus").description("사용 여부 (UNUSED/USED)")
                        )
                ));
    }
}