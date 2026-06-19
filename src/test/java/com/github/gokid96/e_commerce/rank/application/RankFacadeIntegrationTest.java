package com.github.gokid96.e_commerce.rank.application;

import com.github.gokid96.e_commerce.order.domain.Order;
import com.github.gokid96.e_commerce.order.domain.OrderProduct;
import com.github.gokid96.e_commerce.order.domain.OrderRepository;
import com.github.gokid96.e_commerce.rank.domain.RankCommand;
import com.github.gokid96.e_commerce.rank.domain.RankInfo;
import com.github.gokid96.e_commerce.rank.domain.RankRepository;
import com.github.gokid96.e_commerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
public class RankFacadeIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private RankFacade rankFacade;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RankRepository rankRepository;

    @DisplayName("일별 랭킹을 생성한다.")
    @Test
    void createDailyRankAt() {
        // given
        Order order1 = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(1L, "상품1", 10_000L, 2),
                OrderProduct.create(2L, "상품2", 20_000L, 3)));
        Order order2 = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(1L, "상품1", 10_000L, 2),
                OrderProduct.create(3L, "상품3", 30_000L, 4)));
        Order order3 = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(2L, "상품2", 20_000L, 3),
                OrderProduct.create(3L, "상품3", 30_000L, 4)));

        List.of(order1, order2, order3).forEach(o -> {
            o.paid(LocalDateTime.of(2026, 6, 22, 12, 0, 0));
            orderRepository.save(o);
        });

        // when
        rankFacade.createDailyRankAt(LocalDate.of(2026, 6, 23));

        // then
        RankCommand.PopularSellRank command =
                RankCommand.PopularSellRank.of(3, LocalDate.of(2026, 6, 22), LocalDate.of(2026, 6, 23));
        List<RankInfo.PopularProduct> result = rankRepository.findPopularSellRanks(command);
        assertThat(result).hasSize(3)
                .extracting(RankInfo.PopularProduct::getProductId)
                .containsExactly(3L, 2L, 1L); // 상품3(8) > 상품2(6) > 상품1(4)
    }

}
