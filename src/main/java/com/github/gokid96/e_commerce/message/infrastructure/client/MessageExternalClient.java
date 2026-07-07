package com.github.gokid96.e_commerce.message.infrastructure.client;

import com.github.gokid96.e_commerce.message.domain.MessageClient;
import com.github.gokid96.e_commerce.message.domain.MessageCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageExternalClient implements MessageClient {

    private final MessageDataPlatformClient messageDataPlatformClient;

    @Override
    public void sendOrder(MessageCommand.Order message) {
        messageDataPlatformClient.sendOrder(message);
    }
}