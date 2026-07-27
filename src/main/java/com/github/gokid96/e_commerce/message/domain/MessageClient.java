package com.github.gokid96.e_commerce.message.domain;

public interface MessageClient {

    void sendOrder(MessageCommand.Order message);
}