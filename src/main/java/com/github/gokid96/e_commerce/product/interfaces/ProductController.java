package com.github.gokid96.e_commerce.product.interfaces;

import com.github.gokid96.e_commerce.common.ApiResponse;
import com.github.gokid96.e_commerce.product.application.ProductFacade;
import com.github.gokid96.e_commerce.product.application.ProductResult;
import com.github.gokid96.e_commerce.product.interfaces.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacade productFacade;

    @GetMapping
    public ApiResponse<ProductResponse.Products> getProducts() {
        ProductResult.Products products = productFacade.getProducts();
        return ApiResponse.ok(ProductResponse.Products.of(products));
    }

    @GetMapping("/ranks")
    public ApiResponse<ProductResponse.Products> getPopularProducts() {
        ProductResult.Products products = productFacade.getPopularProducts();
        return ApiResponse.ok(ProductResponse.Products.of(products));
    }
}