package com.github.gokid96.e_commerce.product.domain.product;

import java.util.List;

public interface ProductRepository {
    Product save(Product product);

    Product findById(Long productId);

    List<Product> findByIdIn(List<Long> productIds);

    /** 재고를 조인한 조회 결과를 반환한다. */
    List<ProductInfo.Product> findBySellStatusIn(List<ProductSellingStatus> statuses);

    /** 재고를 조인한 조회 결과를 반환한다. */
    List<ProductInfo.Product> findAll(ProductCommand.Query command);
}
