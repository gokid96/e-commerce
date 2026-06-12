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
    private final StockService stockService;

    public ProductInfo.Products getSellingProducts() {
        List<Product> products = productRepository.findBySellStatusIn(ProductSellingStatus.forSelling());

        List<ProductInfo.Product> infos = products.stream()
                .map(product -> {
                    StockInfo.Stock stock = stockService.getStock(product.getId());
                    return ProductInfo.Product.of(product, stock.getQuantity());
                })
                .toList();
        return ProductInfo.Products.of(infos);
    }

    public ProductInfo.OrderProducts getOrderProducts(ProductCommand.OrderProducts command) {
        List<ProductInfo.OrderProduct> orderProducts = command.getProducts().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId());
                    if (product.cannotSelling()) {
                        throw new IllegalArgumentException("판매 중인 상품이 아닙니다.");
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
                .map(product -> {
                    StockInfo.Stock stock = stockService.getStock(product.getId());
                    return ProductInfo.Product.of(product, stock.getQuantity());
                }).toList();
        return ProductInfo.Products.of(products);
    }


}
