package com.github.gokid96.e_commerce.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    CREATED("주문 생성"),
    PAID("결제 완료");

    private final String description;

    private static final List<OrderStatus> CANNOT_PAYABLE = List.of(PAID);

    public boolean cannotPayable() {
        return CANNOT_PAYABLE.contains(this);
    }
}
