package com.github.gokid96.e_commerce.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderRequest {
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
    private Long couponId;
    @Valid
    @NotEmpty(message = "상품 목록은 1개 이상이어야 합니다.")
    private List<OrderProductRequest> products;

    @Getter
    @NoArgsConstructor
    public static class OrderProductRequest {
        @NotNull(message = "상품 ID는 필수입니다.")
        private Long id;
        @NotNull(message = "상품 구매 수량은 필수입니다.")
        @Positive(message = "상품 구매 수량은 양수여야 합니다.")
        private Integer quantity;
    }
}
