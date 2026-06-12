package com.github.gokid96.e_commerce.product.interfaces;

import com.github.gokid96.e_commerce.common.ApiResponse;
import com.github.gokid96.e_commerce.product.application.ProductFacade;
import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import com.github.gokid96.e_commerce.product.interfaces.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductFacade productFacade;

    @GetMapping
    public ApiResponse<ProductResponse.Products> getProducts() {
        ProductInfo.Products info = productService.getSellingProducts();
        return ApiResponse.ok(ProductResponse.Products.of(info));
    }

    @GetMapping("/ranks")
    public ApiResponse<ProductResponse.Products> getPopularProducts() {
        ProductInfo.Products info = productFacade.getPopularProducts();
        return ApiResponse.ok(ProductResponse.Products.of(info));
    }

}
