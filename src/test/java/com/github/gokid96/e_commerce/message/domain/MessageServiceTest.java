package com.github.gokid96.e_commerce.message.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageClient messageClient;

    @DisplayName("주문 메시지를 클라이언트로 전송한다.")
    @Test
    void sendOrder() {
        messageService.sendOrder(MessageCommand.Order.builder().orderId(1L).build());

        verify(messageClient, times(1)).sendOrder(any());
    }
}