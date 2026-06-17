package com.github.gokid96.e_commerce.product.domain.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public StockInfo.Stock getStock(Long productId) {
        Stock stock = stockRepository.findByProductId(productId);
        return StockInfo.Stock.of(stock.getId(), stock.getQuantity());
    }

    @Transactional
    public void deductStock(StockCommand.OrderProducts command) {
        command.getProducts().forEach(this::deductStock);
    }

    private void deductStock(StockCommand.OrderProduct command) {
        Stock stock = stockRepository.findWithLockByProductId(command.getProductId());
        stock.deduct(command.getQuantity());
    }

}
