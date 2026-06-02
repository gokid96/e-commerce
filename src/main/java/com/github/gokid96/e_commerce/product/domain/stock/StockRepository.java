package com.github.gokid96.e_commerce.product.domain.stock;

public interface StockRepository {
    Stock save(Stock stock);
    Stock findByProductId(Long productId);
}
