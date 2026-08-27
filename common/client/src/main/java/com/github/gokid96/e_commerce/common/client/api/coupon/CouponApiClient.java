package com.github.gokid96.e_commerce.common.client.api.coupon;

import com.github.gokid96.e_commerce.common.client.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "coupon-service", url = "${endpoints.coupon-service.url}")
public interface CouponApiClient {

    @GetMapping("/api/v1/coupons/{userCouponId}")
    ApiResponse<CouponResponse.UserCoupon> getUsableCoupon(@PathVariable("userCouponId") Long userCouponId);

    @PostMapping("/api/v1/coupons/use")
    ApiResponse<Void> useUserCoupon(@RequestBody CouponRequest.Use request);

    @PutMapping("/api/v1/coupons/{userCouponId}/cancel")
    ApiResponse<Void> cancelCoupon(@PathVariable("userCouponId") Long userCouponId);
}
