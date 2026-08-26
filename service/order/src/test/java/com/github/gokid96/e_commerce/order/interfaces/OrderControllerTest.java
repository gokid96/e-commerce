package com.github.gokid96.e_commerce.order.interfaces;

import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends ControllerTestSupport {

    @DisplayName("주문을 생성한다.")
    @Test
    void createOrder() throws Exception {
        String content = """
                {
                  "userId": 1,
                  "userCouponId": 50,
                  "products": [ { "productId": 10, "quantity": 2 } ]
                }
                """;

        when(orderService.createOrder(any())).thenReturn(OrderInfo.Order.of(1L, 20000L, 0L));

        mockMvc.perform(post("/api/v1/orders")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("주문 생성 시, 사용자 쿠폰 ID는 선택이다.")
    @Test
    void createOrderWithoutCoupon() throws Exception {
        String content = """
                {
                  "userId": 1,
                  "products": [ { "productId": 10, "quantity": 2 } ]
                }
                """;

        when(orderService.createOrder(any())).thenReturn(OrderInfo.Order.of(1L, 20000L, 0L));

        mockMvc.perform(post("/api/v1/orders")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @DisplayName("주문 생성 시, 사용자 ID는 필수다.")
    @Test
    void createOrderWithoutUserId() throws Exception {
        String content = """
                {
                  "products": [ { "productId": 10, "quantity": 2 } ]
                }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("사용자 ID는 필수입니다."));
    }

    @DisplayName("주문 생성 시, 상품 목록은 1개 이상이어야 한다.")
    @Test
    void createOrderWithEmptyProducts() throws Exception {
        String content = """
                {
                  "userId": 1,
                  "products": []
                }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("상품 목록은 1개 이상이어야 합니다."));
    }

    @DisplayName("주문 생성 시, 상품 구매 수량은 양수여야 한다.")
    @Test
    void createOrderWithZeroQuantity() throws Exception {
        String content = """
                {
                  "userId": 1,
                  "products": [ { "productId": 10, "quantity": 0 } ]
                }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("상품 구매 수량은 양수여야 합니다."));
    }
}