package com.github.gokid96.e_commerce.product.domain.stock;

public interface StockEventPublisher {

    void deducted(StockEvent.Deducted event);

    void deductFailed(StockEvent.DeductFailed event);
}