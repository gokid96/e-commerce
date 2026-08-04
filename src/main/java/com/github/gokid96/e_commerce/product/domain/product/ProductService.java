package com.github.gokid96.e_commerce.product.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductInfo.Products getSellingProducts() {
        List<ProductInfo.Product> infos = productRepository.findBySellStatusIn(ProductSellingStatus.forSelling()).stream()
                .map(ProductInfo.Product::of)
                .toList();
        return ProductInfo.Products.of(infos);
    }

    @Transactional(readOnly = true)
    public ProductInfo.OrderProducts getOrderProducts(ProductCommand.OrderProducts command) {
        List<ProductInfo.OrderProduct> orderProducts = command.getProducts().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId());
                    if (product.cannotSelling()) {
                        throw new IllegalStateException("판매 중인 상품이 아닙니다.");
                    }
                    return ProductInfo.OrderProduct.of(
                            product.getId(), product.getName(), product.getPrice(), item.getQuantity());
                })
                .toList();
        return ProductInfo.OrderProducts.of(orderProducts);
    }

    @Transactional(readOnly = true)
    public ProductInfo.Products getProducts(ProductCommand.Products command) {
        List<ProductInfo.Product> products = command.getProductIds().stream()
                .map(productRepository::findById)
                .map(ProductInfo.Product::of)
                .toList();
        return ProductInfo.Products.of(products);
    }

    @Transactional(readOnly = true)
    public ProductInfo.Products getProducts(ProductCommand.Query command) {
        List<ProductInfo.Product> products = productRepository.findAll(command).stream()
                .map(ProductInfo.Product::of)
                .toList();
        return ProductInfo.Products.of(products);
    }
}