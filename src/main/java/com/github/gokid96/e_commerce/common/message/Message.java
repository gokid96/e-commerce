package com.github.gokid96.e_commerce.common.message;

public interface Message {

    String getTopic();

    String getKey();

    String getPayload();
}
