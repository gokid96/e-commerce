package com.github.gokid96.e_commerce.rank.application;


import com.github.gokid96.e_commerce.order.domain.OrderCommand;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.rank.domain.RankCommand;
import com.github.gokid96.e_commerce.rank.domain.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankFacade {

    private final OrderService orderService;
    private final RankService rankService;

    @Transactional
    public void createDailyRankAt(LocalDate date) {
        OrderCommand.DateQuery orderCommand = OrderCommand.DateQuery.of(date);
        OrderInfo.PaidProducts paidProducts = orderService.getPaidProducts(orderCommand);

        RankCommand.CreateList rankCommand = createListCommand(paidProducts, date);
        rankService.createSellRank(rankCommand);

    }

    private RankCommand.CreateList createListCommand(OrderInfo.PaidProducts paidProducts, LocalDate date) {
        List<RankCommand.Create> commands = paidProducts.getProducts().stream()
                .map(product -> createCommand(product, date))
                .toList();
        return RankCommand.CreateList.of(commands);
    }

    private RankCommand.Create createCommand(OrderInfo.PaidProduct product, LocalDate date) {
        return RankCommand.Create.of(product.getProductId(), product.getQuantity(), date);
    }
}
