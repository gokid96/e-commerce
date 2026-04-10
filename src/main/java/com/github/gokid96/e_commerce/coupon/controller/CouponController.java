package com.github.gokid96.e_commerce.coupon.controller;

import com.github.gokid96.e_commerce.common.ApiResponse;
import com.github.gokid96.e_commerce.coupon.dto.request.CouponRequest;
import com.github.gokid96.e_commerce.coupon.dto.response.CouponResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/coupons")
public class CouponController {

    @GetMapping
    public ApiResponse<List<CouponResponse>> getCoupons(@PathVariable long userId) {
        return new ApiResponse.ok(List.of(CouponResponse.of(1L, "쿠폰명", 0.1, LocalDateTime.now()));
    }

    @PostMapping
    public ApiResponse<CouponResponse> issueCoupon(@PathVariable long userId,
                                                   @RequestBody CouponRequest couponRequest) {

    }
}
