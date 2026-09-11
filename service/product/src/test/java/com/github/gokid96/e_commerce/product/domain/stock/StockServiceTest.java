package com.github.gokid96.e_commerce.product.domain.stock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    @DisplayName("재고 차감 시 요청 순서와 무관하게 productId 오름차순으로 락을 획득한다.")
    @Test
    void deductStockLocksInProductIdOrder() {
        // given - 요청은 3, 1, 2 순서
        given(stockRepository.findWithLockByProductId(anyLong()))
                .willReturn(Stock.create(1L, 100));

        StockCommand.Deduct command = StockCommand.Deduct.of(List.of(
                StockCommand.OrderProduct.of(3L, 1),
                StockCommand.OrderProduct.of(1L, 1),
                StockCommand.OrderProduct.of(2L, 1)
        ));

        // when
        stockService.deductStock(command);

        // then - 락은 1, 2, 3 순서로 획득
        InOrder order = inOrder(stockRepository);
        order.verify(stockRepository).findWithLockByProductId(1L);
        order.verify(stockRepository).findWithLockByProductId(2L);
        order.verify(stockRepository).findWithLockByProductId(3L);
    }

    @DisplayName("재고 복원 시에도 productId 오름차순으로 락을 획득한다.")
    @Test
    void restoreStockLocksInProductIdOrder() {
        given(stockRepository.findWithLockByProductId(anyLong()))
                .willReturn(Stock.create(1L, 100));

        StockCommand.Restore command = StockCommand.Restore.of(List.of(
                StockCommand.OrderProduct.of(2L, 1),
                StockCommand.OrderProduct.of(1L, 1)
        ));

        stockService.restoreStock(command);

        InOrder order = inOrder(stockRepository);
        order.verify(stockRepository).findWithLockByProductId(1L);
        order.verify(stockRepository).findWithLockByProductId(2L);
    }
}