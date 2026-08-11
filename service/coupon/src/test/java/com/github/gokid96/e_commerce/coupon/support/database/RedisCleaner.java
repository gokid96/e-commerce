package com.github.gokid96.e_commerce.coupon.support.database;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
@RequiredArgsConstructor
public class RedisCleaner {

    private final RedisConnectionFactory connectionFactory;

    public void clean() {
        connectionFactory.getConnection().serverCommands().flushAll();
    }
}
