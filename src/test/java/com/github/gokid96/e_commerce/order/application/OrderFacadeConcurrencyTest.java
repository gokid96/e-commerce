package com.github.gokid96.e_commerce.order.application;

import com.github.gokid96.e_commerce.balance.domain.Balance;
import com.github.gokid96.e_commerce.balance.domain.BalanceRepository;
import com.github.gokid96.e_commerce.product.domain.product.Product;
import com.github.gokid96.e_commerce.product.domain.product.ProductRepository;
import com.github.gokid96.e_commerce.product.domain.product.ProductSellingStatus;
import com.github.gokid96.e_commerce.product.domain.stock.Stock;
import com.github.gokid96.e_commerce.product.domain.stock.StockRepository;
import com.github.gokid96.e_commerce.support.ConcurrencyTestSupport;
import com.github.gokid96.e_commerce.user.domain.User;
import com.github.gokid96.e_commerce.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderFacadeConcurrencyTest extends ConcurrencyTestSupport {
    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StockRepository stockRepository;

    @DisplayName("동시성 - 모든 주순이 정상 처리 되어야 한다.")
    @Test
    void createOrderConcurrency() {
        // given
        User user = User.create("유저");
        userRepository.save(user);


        Balance balance = Balance.create(user.getId(), 500_000L);
        balanceRepository.save(balance);

        Product product = Product.create("상품", 100_000L, ProductSellingStatus.SELLING);

        Stock stock = Stock.create(product.getId(), 100);
        stockRepository.save(stock);

        OrderCriteria.Create criteria = OrderCriteria.Create.of(user.getId(), null,
                List.of(OrderCriteria.OrderProduct.of(product.getId(), 1)));

        // when
        executeConcurrency(3, () -> orderFacade.createOrder(criteria));

        // then
        Balance remainBalance = balanceRepository.findOptionalByUserId(user.getId()).orElseThrow();
        Stock remainStock = stockRepository.findByProductId(product.getId());

        assertThat(remainBalance.getAmount()).isEqualTo(200_000L);
        assertThat(remainStock.getQuantity()).isEqualTo(97);


    }

}
