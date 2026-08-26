package com.github.gokid96.e_commerce.common.client.api.coupon;

import com.github.gokid96.e_commerce.common.client.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "coupon-service", url = "${endpoints.coupon-service.url}")
public interface CouponApiClient {

    @PostMapping("/api/v1/coupons/use")
    ApiResponse<Void> useUserCoupon(@RequestBody CouponRequest.Use request);
}
