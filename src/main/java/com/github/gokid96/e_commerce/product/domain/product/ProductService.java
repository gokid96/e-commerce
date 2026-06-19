package com.github.gokid96.e_commerce.product.domain.product;

import com.github.gokid96.e_commerce.product.domain.stock.StockInfo;
import com.github.gokid96.e_commerce.product.domain.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductInfo.Products getSellingProducts() {
        List<ProductInfo.Product> infos = productRepository.findBySellStatusIn(ProductSellingStatus.forSelling()).stream()
                .map(ProductInfo.Product::of)
                .toList();
        return ProductInfo.Products.of(infos);
    }

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

    public ProductInfo.Products getProducts(ProductCommand.Products command) {
        List<ProductInfo.Product> products = command.getProductIds().stream()
                .map(productRepository::findById)
                .map(ProductInfo.Product::of)
                .toList();
        return ProductInfo.Products.of(products);
    }


}
