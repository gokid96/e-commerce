package com.github.gokid96.e_commerce.product.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum ProductSellingStatus {

    HOLD("판매 보류"),
    SELLING("판매 중"),
    STOP_SELLING("판매 중지");

    private final String description;

    private static final List<ProductSellingStatus> CANNOT_SELLING_STATUS = List.of(HOLD, STOP_SELLING);

    public boolean cannotSelling(){
        return CANNOT_SELLING_STATUS.contains(this);
    }

    public static List<ProductSellingStatus> forSelling(){
        return List.of(SELLING);
    }

}
