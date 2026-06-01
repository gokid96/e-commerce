package com.github.gokid96.e_commerce.product.domain.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockInfo {

    @Getter
    public static class Stock{
        private final Long stockId;
        private final int quantity;

        private Stock(Long stockId, int quantity){
            this.stockId = stockId;
            this.quantity = quantity;
        }
        public static Stock of(Long stockId, int quantity){
            return new Stock(stockId, quantity);
        }
    }
}
