package com.github.gokid96.e_commerce.rank.application;

import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.rank.domain.RankService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class RankFacadeUnitTest {

    @InjectMocks
    private RankFacade rankFacade;

    @Mock
    private OrderService orderService;

    @Mock
    private RankService rankService;

    @DisplayName("일별 랭킹을 생성한다.")
    @Test
    void createDailyRankAt() {
        // given
        OrderInfo.PaidProducts paidProducts = OrderInfo.PaidProducts.of(List.of(
                OrderInfo.PaidProduct.of(1L, 10),
                OrderInfo.PaidProduct.of(2L, 20)
        ));
        given(orderService.getPaidProducts(any())).willReturn(paidProducts);
        // when
        rankFacade.createDailyRankAt(LocalDate.now().minusDays(1));

        // then
        InOrder inOrder = inOrder(orderService, rankService);
        inOrder.verify(orderService, times(1)).getPaidProducts(any());
        inOrder.verify(rankService, times(1)).createSellRank(any());
    }

}
