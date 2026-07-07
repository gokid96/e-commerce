package com.github.gokid96.e_commerce.message.infrastructure.client;

import com.github.gokid96.e_commerce.message.domain.MessageCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 외부 데이터 플랫폼 전송 흉내. 추후 Kafka 발행으로 대체된다.
@Slf4j
@Component
public class MessageDataPlatformClient {

    public void sendOrder(MessageCommand.Order message) {
        log.info("외부 데이터 플랫폼 주문 정보 전송: orderId={}", message.getOrderId());
    }
}
