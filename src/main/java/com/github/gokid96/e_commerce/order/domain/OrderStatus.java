package com.github.gokid96.e_commerce.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    CREATED("주문 생성"),
    PAID("결제 완료");

    private final String description;
}
