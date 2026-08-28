package com.github.gokid96.e_commerce.product.interfaces.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class ProductInternalRequest {

    @Getter
    @NoArgsConstructor
    public static class Products {
        private List<Long> productIds;
    }
}