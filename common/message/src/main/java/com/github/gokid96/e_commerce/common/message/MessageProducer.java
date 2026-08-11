package com.github.gokid96.e_commerce.common.message;

public interface MessageProducer {

    void send(Message message);

    void sendSync(Message message) throws Exception;
}