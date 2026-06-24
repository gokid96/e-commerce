package com.github.gokid96.e_commerce.common.cache.infrastructure;


import com.github.gokid96.e_commerce.common.cache.CacheTemplate;
import com.github.gokid96.e_commerce.common.cache.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;

import static java.lang.Boolean.FALSE;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheTemplate implements CacheTemplate {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder().build();

    private final StringRedisTemplate redisTemplate;

    @Override
    public <T> Optional<T> get(Cacheable cacheable, String key, Class<T> type) {
        String createKey = cacheable.createKey(key);
        return Optional.ofNullable(redisTemplate.opsForValue().get(createKey))
                .map(value -> OBJECT_MAPPER.readValue(value, type));
    }

    @Override
    public <T> void put(Cacheable cacheable, String key, T value) {
        String createKey = cacheable.createKey(key);
        redisTemplate.opsForValue().set(createKey, OBJECT_MAPPER.writeValueAsString(value), cacheable.ttl());
    }

    @Override
    public void evict(Cacheable cacheable, String key) {
        String createKey = cacheable.createKey(key);
        Boolean deleted = redisTemplate.delete(createKey);
        if (FALSE.equals(deleted)) {
            log.debug("삭제할 캐시가 존재하지 않습니다. key: {}", createKey);
        }
    }
}
