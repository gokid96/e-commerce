package com.github.gokid96.e_commerce.product.interfaces;

import com.github.gokid96.e_commerce.product.domain.product.ProductCommand;
import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import com.github.gokid96.e_commerce.product.domain.stock.StockService;
import com.github.gokid96.e_commerce.product.interfaces.dto.ProductInternalRequest;
import com.github.gokid96.e_commerce.product.interfaces.dto.ProductStockResponse;
import com.github.gokid96.e_commerce.product.interfaces.dto.StockInternalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductInternalController {

    private final ProductService productService;
    private final StockService stockService;

    @PostMapping("/api/v1/products/list")
    public ApiResponse<ProductStockResponse.Products> getProducts(@RequestBody ProductInternalRequest.Products request) {
        ProductInfo.Products products = productService.getProducts(ProductCommand.Products.of(request.getProductIds()));
        List<ProductStockResponse.Product> list = products.getProducts().stream()
                .map(p -> ProductStockResponse.Product.of(p, stockService.getQuantity(p.getProductId())))
                .toList();
        return ApiResponse.ok(ProductStockResponse.Products.of(list));
    }

    @PostMapping("/api/v1/products/stocks/deduct")
    public ApiResponse<Void> deductStock(@RequestBody StockInternalRequest.Deduct request) {
        stockService.deductStock(request.toCommand());
        return ApiResponse.ok();
    }

    @PostMapping("/api/v1/products/stocks/restore")
    public ApiResponse<Void> restoreStock(@RequestBody StockInternalRequest.Restore request) {
        stockService.restoreStock(request.toCommand());
        return ApiResponse.ok();
    }
}