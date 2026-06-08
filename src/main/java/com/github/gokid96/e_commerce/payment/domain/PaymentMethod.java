package com.github.gokid96.e_commerce.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {

    BALANCE("잔액"),
    KAKAO_PAY("카카오페이"),
    TOSS_PAY("토스페이"),
    NAVER_PAY("네이버페이");

    private final String description;
}
