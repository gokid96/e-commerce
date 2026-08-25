package com.github.gokid96.e_commerce.product.domain.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public void deductStock(StockCommand.Deduct command) {
        command.getProducts().forEach(this::deductStock);
    }

    @Transactional
    public void restoreStock(StockCommand.Restore command) {
        command.getProducts().forEach(this::restoreStock);
    }

    private void deductStock(StockCommand.OrderProduct command) {
        Stock stock = stockRepository.findWithLockByProductId(command.getProductId());
        stock.deduct(command.getQuantity());
    }

    private void restoreStock(StockCommand.OrderProduct command) {
        Stock stock = stockRepository.findWithLockByProductId(command.getProductId());
        stock.restore(command.getQuantity());
    }
}