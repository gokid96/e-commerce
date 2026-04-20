package com.github.gokid96.e_commerce.product.controller;

import com.github.gokid96.e_commerce.common.ApiResponse;
import com.github.gokid96.e_commerce.product.dto.response.ProductRankResponse;
import com.github.gokid96.e_commerce.product.dto.response.ProductResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @GetMapping
    public ApiResponse<List<ProductResponse>> getProducts() {
        return ApiResponse.ok(List.of(ProductResponse.of(1L, "상품명", 30000L, 100L)));
    }

    @GetMapping("/ranks")
    public ApiResponse<List<ProductRankResponse>> ranks() {
        return ApiResponse.ok(List.of(ProductRankResponse.of(1L, "상품명", 30000L, 150L)));
    }
}
