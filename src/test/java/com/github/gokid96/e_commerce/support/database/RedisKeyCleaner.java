package com.github.gokid96.e_commerce.support.database;

import com.github.gokid96.e_commerce.common.key.KeyType;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Set;

@Component
@Profile("test")
public class RedisKeyCleaner {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisKeyCleaner(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void clean() {
        Arrays.stream(KeyType.values())
                .map(KeyType::getKey)
                .map(name -> stringRedisTemplate.keys(name + "*"))
                .filter(keys -> !CollectionUtils.isEmpty(keys))
                .forEach(stringRedisTemplate::delete);
    }
}