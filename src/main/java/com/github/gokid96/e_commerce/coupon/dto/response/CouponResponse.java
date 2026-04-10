package com.github.gokid96.e_commerce.coupon.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResponse {
    private Long id;
    private String name;
    private Double discountRate;
    private LocalDateTime expiredAt;

    public static CouponResponse of(Long id, String name, Double discountRate, LocalDateTime expiredAt) {
        return new CouponResponse(id, name, discountRate, expiredAt);
    }

}
