package com.github.gokid96.e_commerce.product.domain.stock;

import com.github.gokid96.e_commerce.support.ConcurrencyTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class StockServiceConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @DisplayName("동시에 재고를 차감하면 모든 요청이 정상 차감 된다.")
    @Test
    void deductStockWithPessimisticWriteLock() {
        // given
        stockRepository.save(Stock.create(1L, 10));

        StockCommand.OrderProducts command = StockCommand.OrderProducts.of(
                List.of(StockCommand.OrderProduct.of(1L, 1))
        );

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        executeConcurrency(2, () -> {
            try {
                stockService.deductStock(command);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        // then
        assertThat(successCount.get()).isEqualTo(2);
        assertThat(failCount.get()).isEqualTo(0);

        Stock remainStock = stockRepository.findByProductId(1L);
        assertThat(remainStock.getQuantity()).isEqualTo(8);
    }

    @DisplayName("동시에 재고를 차감할 때 재고가 부족하면 예외가 발생한다.")
    @Test
    void deductStockWhenInsufficient() {
        // given
        stockRepository.save(Stock.create(1L, 1));

        StockCommand.OrderProducts command = StockCommand.OrderProducts.of(
                List.of(StockCommand.OrderProduct.of(1L, 1))
        );

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        //when
        executeConcurrency(2, () -> {
            try {
                stockService.deductStock(command);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        Stock remainStock = stockRepository.findByProductId(1L);
        assertThat(remainStock.getQuantity()).isZero();
    }
}
