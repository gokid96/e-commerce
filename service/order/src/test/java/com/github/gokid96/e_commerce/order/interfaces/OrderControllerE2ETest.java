package com.github.gokid96.e_commerce.order.interfaces;

import com.github.gokid96.e_commerce.order.domain.OrderClient;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.support.E2EControllerTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrderControllerE2ETest extends E2EControllerTestSupport {

    @MockitoBean
    private OrderClient orderClient;

    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        when(orderClient.getProducts(any())).thenReturn(List.of(
                OrderInfo.Product.of(10L, "상품1", 100_000L, 100),
                OrderInfo.Product.of(20L, "상품2", 200_000L, 200)
        ));
    }

    @DisplayName("주문을 접수하면 성공한다.")
    @Test
    void orderPayment() {
        Map<String, Object> request = Map.of(
                "userId", USER_ID,
                "products", List.of(
                        Map.of("productId", 10L, "quantity", 1),
                        Map.of("productId", 20L, "quantity", 2)));

        client.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.message").isEqualTo("OK");
    }
}
