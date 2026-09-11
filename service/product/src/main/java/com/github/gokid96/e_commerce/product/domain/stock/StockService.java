package com.github.gokid96.e_commerce.product.domain.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public void deductStock(StockCommand.Deduct command) {
        lockOrdered(command.getProducts()).forEach(this::deductStock);
    }

    @Transactional
    public void restoreStock(StockCommand.Restore command) {
        lockOrdered(command.getProducts()).forEach(this::restoreStock);
    }

    private List<StockCommand.OrderProduct> lockOrdered(List<StockCommand.OrderProduct> products) {
        return products.stream()
                .sorted(Comparator.comparing(StockCommand.OrderProduct::getProductId))
                .toList();
    }

    private void deductStock(StockCommand.OrderProduct command) {
        Stock stock = stockRepository.findWithLockByProductId(command.getProductId());
        stock.deduct(command.getQuantity());
    }

    private void restoreStock(StockCommand.OrderProduct command) {
        Stock stock = stockRepository.findWithLockByProductId(command.getProductId());
        stock.restore(command.getQuantity());
    }

    @Transactional(readOnly = true)
    public int getQuantity(Long productId) {
        return stockRepository.findByProductId(productId).getQuantity();
    }
}