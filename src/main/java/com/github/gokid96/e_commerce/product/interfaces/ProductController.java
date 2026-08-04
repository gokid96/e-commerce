package com.github.gokid96.e_commerce.product.interfaces;

import com.github.gokid96.e_commerce.common.ApiResponse;
import com.github.gokid96.e_commerce.product.application.ProductFacade;
import com.github.gokid96.e_commerce.product.application.ProductResult;
import com.github.gokid96.e_commerce.product.domain.product.ProductCommand;
import com.github.gokid96.e_commerce.product.interfaces.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacade productFacade;

    @GetMapping("/api/v1/products")
    public ApiResponse<ProductResponse.Products> getProducts() {
        ProductResult.Products products = productFacade.getProducts();
        return ApiResponse.ok(ProductResponse.Products.of(products));
    }

    @GetMapping("/api/v2/products")
    public ApiResponse<ProductResponse.Products> getProducts(
            @RequestParam("pageSize") Long pageSize,
            @RequestParam(value = "cursor", required = false) Long cursor
    ) {
        ProductResult.Products products = productFacade.getProducts(ProductCommand.Query.of(pageSize, cursor));
        return ApiResponse.ok(ProductResponse.Products.of(products));
    }
}