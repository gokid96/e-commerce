package com.github.gokid96.e_commerce.product.domain.product;

import java.util.List;

public interface ProductRepository {

    Product save(Product product);

    List<Product> findBySellStatusIn(List<ProductSellingStatus> statuses);
}
