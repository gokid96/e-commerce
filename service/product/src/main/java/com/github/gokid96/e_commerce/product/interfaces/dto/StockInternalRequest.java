package com.github.gokid96.e_commerce.product.interfaces.dto;

import com.github.gokid96.e_commerce.product.domain.stock.StockCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class StockInternalRequest {

    @Getter
    @NoArgsConstructor
    public static class Deduct {
        private List<Product> products;

        public StockCommand.Deduct toCommand() {
            return StockCommand.Deduct.of(products.stream()
                    .map(p -> StockCommand.OrderProduct.of(p.getProductId(), p.getQuantity()))
                    .toList());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Restore {
        private List<Product> products;

        public StockCommand.Restore toCommand() {
            return StockCommand.Restore.of(products.stream()
                    .map(p -> StockCommand.OrderProduct.of(p.getProductId(), p.getQuantity()))
                    .toList());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Product {
        private Long productId;
        private int quantity;
    }
}