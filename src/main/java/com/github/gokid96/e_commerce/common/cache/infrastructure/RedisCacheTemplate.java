package com.github.gokid96.e_commerce.common.cache.infrastructure;

import com.github.gokid96.e_commerce.common.cache.CacheTemplate;
import com.github.gokid96.e_commerce.common.cache.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static java.lang.Boolean.FALSE;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheTemplate implements CacheTemplate {

    // @Cacheable(RedisCacheManager)과 동일한 직렬화기를 사용 → 같은 @class 포맷으로 읽고 쓴다.
    private static final GenericJacksonJsonRedisSerializer SERIALIZER =
            GenericJacksonJsonRedisSerializer.builder()
                    .enableSpringCacheNullValueSupport()
                    .enableUnsafeDefaultTyping()
                    .build();

    private final StringRedisTemplate redisTemplate;

    @Override
    public <T> Optional<T> get(Cacheable cacheable, String key, Class<T> type) {
        String value = redisTemplate.opsForValue().get(cacheable.createKey(key));
        return Optional.ofNullable(value)
                .map(v -> SERIALIZER.deserialize(v.getBytes(StandardCharsets.UTF_8), type));
    }

    @Override
    public <T> void put(Cacheable cacheable, String key, T value) {
        byte[] bytes = SERIALIZER.serialize(value);
        redisTemplate.opsForValue().set(
                cacheable.createKey(key), new String(bytes, StandardCharsets.UTF_8), cacheable.ttl());
    }

    @Override
    public void evict(Cacheable cacheable, String key) {
        String createdKey = cacheable.createKey(key);
        Boolean deleted = redisTemplate.delete(createdKey);
        if (FALSE.equals(deleted)) {
            log.debug("삭제할 캐시가 존재하지 않습니다. key: {}", createdKey);
        }
    }
}
