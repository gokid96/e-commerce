package com.github.gokid96.e_commerce.product.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResponse {
    private long id;
    private String name;
    private long price;
    private long stock;

    public static ProductResponse of(long id, String name, long price, long stock) {
        return new ProductResponse(id, name, price, stock);
    }
}
