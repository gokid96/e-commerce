package com.github.gokid96.e_commerce.product.infrastructure;

import com.github.gokid96.e_commerce.product.domain.product.Product;
import com.github.gokid96.e_commerce.product.domain.product.ProductCommand;
import com.github.gokid96.e_commerce.product.domain.product.ProductRepository;
import com.github.gokid96.e_commerce.product.domain.product.ProductSellingStatus;
import com.github.gokid96.e_commerce.product.infrastructure.jpa.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductCoreRepository implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductQueryDslRepository productQueryDslRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public List<Product> findBySellStatusIn(List<ProductSellingStatus> statuses) {
        return productJpaRepository.findBySellStatusIn(statuses);
    }

    @Override
    public Product findById(Long productId){
        return productJpaRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));
    }

    @Override
    public List<Product> findAll(ProductCommand.Query command) {
        return productQueryDslRepository.findAll(command);
    }
}