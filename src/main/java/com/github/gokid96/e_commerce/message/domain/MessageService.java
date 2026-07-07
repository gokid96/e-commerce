package com.github.gokid96.e_commerce.message.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageClient messageClient;

    public void sendOrder(MessageCommand.Order message) {
        messageClient.sendOrder(message);
    }
}