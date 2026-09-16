package com.github.gokid96.e_commerce.product.rank.interfaces.event;

import com.github.gokid96.e_commerce.product.rank.domain.RankCommand;
import com.github.gokid96.e_commerce.product.rank.domain.RankService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RankOrderMessageEventListenerTest {

    @InjectMocks
    private RankOrderMessageEventListener rankOrderMessageEventListener;

    @Mock
    private RankService rankService;

    @Captor
    private ArgumentCaptor<RankCommand.CreateList> commandCaptor;

    private static final String MESSAGE = """
            {
                "eventId": "6f1c2b4e-7a91-4d5c-9f30-2a8b1c6d4e77",
                "eventType": "ORDER_COMPLETED",
                "payload": {
                    "orderProducts": [
                        { "productId": 1, "quantity": 2 },
                        { "productId": 2, "quantity": 5 }
                    ]
                }
            }
            """;

    @DisplayName("주문 완료 이벤트를 수신하면 판매 랭크를 집계하고 오프셋을 커밋한다.")
    @Test
    void handleOrderCompleted() {
        Acknowledgment ack = mock(Acknowledgment.class);

        rankOrderMessageEventListener.handleOrderCompleted(MESSAGE, ack);

        verify(rankService).createSellRank(commandCaptor.capture());
        assertThat(commandCaptor.getValue().getRanks())
                .extracting("productId", "score", "rankDate")
                .containsExactly(
                        tuple(1L, 2L, LocalDate.now()),
                        tuple(2L, 5L, LocalDate.now())
                );
        verify(ack).acknowledge();
    }

    @DisplayName("주문 상품이 없으면 빈 집계로 처리하고 오프셋을 커밋한다.")
    @Test
    void handleOrderCompletedWithEmptyProducts() {
        String message = """
                {
                    "eventId": "6f1c2b4e-7a91-4d5c-9f30-2a8b1c6d4e77",
                    "eventType": "ORDER_COMPLETED",
                    "payload": {
                        "orderProducts": []
                    }
                }
                """;
        Acknowledgment ack = mock(Acknowledgment.class);

        rankOrderMessageEventListener.handleOrderCompleted(message, ack);

        verify(rankService).createSellRank(commandCaptor.capture());
        assertThat(commandCaptor.getValue().getRanks()).isEmpty();
        verify(ack).acknowledge();
    }

    @DisplayName("집계에 실패하면 오프셋을 커밋하지 않고 예외를 전파한다.")
    @Test
    void handleOrderCompletedWhenFailed() {
        Acknowledgment ack = mock(Acknowledgment.class);
        willThrow(new IllegalStateException("집계 실패"))
                .given(rankService).createSellRank(any(RankCommand.CreateList.class));

        assertThatThrownBy(() -> rankOrderMessageEventListener.handleOrderCompleted(MESSAGE, ack))
                .isInstanceOf(IllegalStateException.class);

        verify(ack, never()).acknowledge();
    }
}
