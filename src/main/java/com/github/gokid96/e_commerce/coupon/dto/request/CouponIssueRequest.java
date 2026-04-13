package com.github.gokid96.e_commerce.coupon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CouponIssueRequest {
    @NotNull(message = "쿠폰 ID는 필수입니다.")
    private Long couponId;
}
