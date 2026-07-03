package com.github.gokid96.e_commerce.product.application;

import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 판매 상품 목록 조회만 담당. 인기상품 조회는 RankFacade
@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;

    @Transactional(readOnly = true)
    public ProductResult.Products getProducts() {
        ProductInfo.Products products = productService.getSellingProducts();
        return ProductResult.Products.of(products.getProducts().stream()
                .map(this::toProductResult)
                .toList());
    }

    private ProductResult.Product toProductResult(ProductInfo.Product product) {
        return ProductResult.Product.of(
                product.getProductId(), product.getProductName(), product.getProductPrice());
    }
}