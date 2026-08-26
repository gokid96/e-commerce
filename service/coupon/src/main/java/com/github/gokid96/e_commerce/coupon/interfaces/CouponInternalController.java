package com.github.gokid96.e_commerce.coupon.interfaces;

import com.github.gokid96.e_commerce.coupon.application.CouponFacade;
import com.github.gokid96.e_commerce.coupon.interfaces.dto.CouponRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CouponInternalController {

    private final CouponFacade couponFacade;

    @PostMapping("/api/v1/coupons/use")
    public ApiResponse<Void> useUserCoupon(@Valid @RequestBody CouponRequest.Use request) {
        couponFacade.useUserCoupon(request.getUserCouponId());
        return ApiResponse.ok();
    }
}
