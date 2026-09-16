package com.github.gokid96.e_commerce.product.domain.stock;

import com.github.gokid96.e_commerce.product.support.IntegrationTestSupport;
import com.github.gokid96.e_commerce.product.support.database.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired private StockService stockService;
    @Autowired private StockRepository stockRepository;
    @Autowired private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.clean();
    }

    @DisplayName("여러 상품의 재고를 한 번에 차감한다.")
    @Test
    void deductStock() {
        stockRepository.save(Stock.create(1L, 10));
        stockRepository.save(Stock.create(2L, 3));

        stockService.deductStock(StockCommand.Deduct.of(List.of(
                StockCommand.OrderProduct.of(1L, 3),
                StockCommand.OrderProduct.of(2L, 3)
        )));

        assertThat(stockRepository.findByProductId(1L).getQuantity()).isEqualTo(7);
        assertThat(stockRepository.findByProductId(2L).getQuantity()).isZero();
    }

    @DisplayName("재고가 없는 상품은 차감할 수 없다.")
    @Test
    void deductStockWhenNotExists() {
        StockCommand.Deduct command = StockCommand.Deduct.of(List.of(
                StockCommand.OrderProduct.of(1L, 1)
        ));

        assertThatThrownBy(() -> stockService.deductStock(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 존재하지 않습니다.");
    }

    @DisplayName("재고가 부족하면 차감할 수 없다.")
    @Test
    void deductStockWhenInsufficient() {
        stockRepository.save(Stock.create(1L, 1));

        StockCommand.Deduct command = StockCommand.Deduct.of(List.of(
                StockCommand.OrderProduct.of(1L, 2)
        ));

        assertThatThrownBy(() -> stockService.deductStock(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족합니다.");
    }

    @DisplayName("여러 상품 중 하나라도 재고가 부족하면 아무것도 차감되지 않는다.")
    @Test
    void deductStockRollsBackWhenAnyInsufficient() {
        stockRepository.save(Stock.create(1L, 10));
        stockRepository.save(Stock.create(2L, 1));

        StockCommand.Deduct command = StockCommand.Deduct.of(List.of(
                StockCommand.OrderProduct.of(1L, 3),
                StockCommand.OrderProduct.of(2L, 5)
        ));

        assertThatThrownBy(() -> stockService.deductStock(command))
                .isInstanceOf(IllegalArgumentException.class);

        // 트랜잭션이 롤백되어 앞선 상품의 차감도 반영되지 않아야 한다.
        assertThat(stockRepository.findByProductId(1L).getQuantity()).isEqualTo(10);
        assertThat(stockRepository.findByProductId(2L).getQuantity()).isEqualTo(1);
    }

    @DisplayName("여러 상품의 재고를 한 번에 복구한다.")
    @Test
    void restoreStock() {
        stockRepository.save(Stock.create(1L, 10));
        stockRepository.save(Stock.create(2L, 3));

        stockService.restoreStock(StockCommand.Restore.of(List.of(
                StockCommand.OrderProduct.of(1L, 3),
                StockCommand.OrderProduct.of(2L, 3)
        )));

        assertThat(stockRepository.findByProductId(1L).getQuantity()).isEqualTo(13);
        assertThat(stockRepository.findByProductId(2L).getQuantity()).isEqualTo(6);
    }

    @DisplayName("재고가 없는 상품은 복구할 수 없다.")
    @Test
    void restoreStockWhenNotExists() {
        StockCommand.Restore command = StockCommand.Restore.of(List.of(
                StockCommand.OrderProduct.of(1L, 1)
        ));

        assertThatThrownBy(() -> stockService.restoreStock(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 존재하지 않습니다.");
    }
}
