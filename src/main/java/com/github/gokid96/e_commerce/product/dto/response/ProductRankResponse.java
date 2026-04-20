package com.github.gokid96.e_commerce.product.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductRankResponse {
    private long id;
    private String name;
    private long price;
    private long saleCount;

    public static ProductRankResponse of(long id, String name, long price, long saleCount) {
        return new ProductRankResponse(id, name, price, saleCount);
    }
}
