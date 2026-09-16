package com.github.gokid96.e_commerce.product.interfaces.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class ProductInternalRequest {

    @Getter
    @NoArgsConstructor
    public static class Products {

        @NotEmpty(message = "상품 ID 목록은 비어 있을 수 없습니다.")
        private List<Long> productIds;
    }
}
