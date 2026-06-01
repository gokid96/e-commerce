package com.github.gokid96.e_commerce.product.infrastructure;

import com.github.gokid96.e_commerce.product.domain.stock.Stock;
import com.github.gokid96.e_commerce.product.domain.stock.StockRepository;
import com.github.gokid96.e_commerce.product.infrastructure.jpa.StockJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockCoreRepository implements StockRepository {
    private final StockJpaRepository stockJpaRepository;

    @Override
    public Stock save(Stock stock) {
        return stockJpaRepository.save(stock);
    }

    @Override
    public Stock findByProductId(Long productId) {
        return stockJpaRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("재고가 존재하지 않습니다."));
    }
}
