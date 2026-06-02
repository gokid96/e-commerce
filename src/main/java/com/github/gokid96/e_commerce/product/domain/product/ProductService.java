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


}
