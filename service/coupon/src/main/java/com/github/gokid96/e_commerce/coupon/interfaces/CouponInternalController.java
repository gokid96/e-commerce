package com.github.gokid96.e_commerce.coupon.interfaces;

import com.github.gokid96.e_commerce.coupon.application.CouponFacade;
import com.github.gokid96.e_commerce.coupon.interfaces.dto.CouponRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PutMapping("/api/v1/coupons/{id}/cancel")
    public ApiResponse<Void> cancelUserCoupon(@PathVariable("id") Long id) {
        couponFacade.cancelUserCoupon(id);
        return ApiResponse.ok();
    }
}
