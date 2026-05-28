package com.github.gokid96.e_commerce.coupon.interfaces;

import com.github.gokid96.e_commerce.common.ApiResponse;
import com.github.gokid96.e_commerce.coupon.application.CouponFacade;
import com.github.gokid96.e_commerce.coupon.application.CouponResult;
import com.github.gokid96.e_commerce.coupon.interfaces.dto.CouponRequest;
import com.github.gokid96.e_commerce.coupon.interfaces.dto.CouponResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class CouponController {

    private final CouponFacade couponFacade;

    @PostMapping("/{userId}/coupons/issue")
    public ApiResponse<Void> issueCoupon(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody CouponRequest.Issue request
    ) {
        couponFacade.issueCoupon(request.toCriteria(userId));
        return ApiResponse.ok();
    }

    @PostMapping("/{userId}/coupons/use")
    public ApiResponse<Void> useCoupon(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody CouponRequest.Use request
    ){
        couponFacade.useCoupon(request.toCriteria(userId));
        return ApiResponse.ok();
    }

    @GetMapping("/{userId}/coupons")
    public ApiResponse<List<CouponResponse.UserCoupon>> getUserCoupons(
            @PathVariable("userId") Long userId
    ){
        List<CouponResult.UserCoupon> results = couponFacade.getUserCoupons(userId);
        List<CouponResponse.UserCoupon> responses = results.stream()
                .map(CouponResponse.UserCoupon::of)
                .toList();
        return ApiResponse.ok(responses);
    }


}
