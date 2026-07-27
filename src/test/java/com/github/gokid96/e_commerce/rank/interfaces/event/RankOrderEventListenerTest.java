package com.github.gokid96.e_commerce.rank.interfaces.event;

import com.github.gokid96.e_commerce.order.domain.OrderEvent;
import com.github.gokid96.e_commerce.rank.domain.RankService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankOrderEventListenerTest {

    @InjectMocks
    private RankOrderEventListener rankOrderEventListener;

    @Mock
    private RankService rankService;

    @DisplayName("주문 완료 시, 랭킹 정보를 업데이트한다.")
    @Test
    void handleCompleted() {
        // given
        OrderEvent.Completed event = mock(OrderEvent.Completed.class);

        // when
        rankOrderEventListener.handle(event);

        // then
        verify(rankService, times(1)).createSellRank(any());
    }
}