package com.github.gokid96.e_commerce.common.client.api.order;

import com.github.gokid96.e_commerce.common.client.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "order-service", url = "${endpoints.order-service.url}")
public interface OrderApiClient {

    @GetMapping("/api/v1/orders/{orderId}")
    ApiResponse<OrderResponse.Order> getOrder(@PathVariable("orderId") Long orderId);

    @PutMapping("/api/v1/coupons/{userCouponId}/cancel")
    ApiResponse<Void> cancelCoupon(@PathVariable("userCouponId") Long userCouponId);
}