package com.github.gokid96.e_commerce.product.domain.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    public StockInfo.Stock getStock(Long productId){
        Stock stock = stockRepository.findByProductId(productId);
        return StockInfo.Stock.of(stock.getId(), stock.getQuantity());
    }

}
