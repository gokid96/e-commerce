package com.github.gokid96.e_commerce.common.client.api.product;

import com.github.gokid96.e_commerce.common.client.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service", url = "${endpoints.product-service.url}")
public interface ProductApiClient {

    @PostMapping("/api/v1/products/list")
    ApiResponse<ProductResponse.Products> getProducts(@RequestBody ProductRequest.Products request);

    @PostMapping("/api/v1/products/stocks/deduct")
    ApiResponse<Void> deductStock(@RequestBody StockRequest.Deduct request);

    @PostMapping("/api/v1/products/stocks/restore")
    ApiResponse<Void> restoreStock(@RequestBody StockRequest.Restore request);
}
