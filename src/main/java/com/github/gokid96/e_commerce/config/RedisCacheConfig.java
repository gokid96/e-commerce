// @Cacheable 동작에 필요한 핵심 설정. @EnableCaching + 캐시별 TTL 등록 + 값 직렬화 방식 지정.
// CacheType.values()를 돌며 "캐시이름 -> TTL" 설정을 RedisCacheManager에 등록한다.
// 값 직렬화: GenericJacksonJsonRedisSerializer(타입정보 @class 포함)로 POJO 왕복 역직렬화 보장.
package com.github.gokid96.e_commerce.config;

import com.github.gokid96.e_commerce.common.cache.CacheType;
import com.github.gokid96.e_commerce.common.cache.Cacheable;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    private static final Duration DEFAULT_TTL = Duration.ofHours(1);

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = getConfigWith(DEFAULT_TTL);

        Map<String, RedisCacheConfiguration> configs = Arrays.stream(CacheType.values())
                .map(this::createConfig)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configs)
                .build();
    }

    private Map.Entry<String, RedisCacheConfiguration> createConfig(Cacheable cacheable) {
        return Map.entry(cacheable.cacheName(), getConfigWith(cacheable.ttl()));
    }

    private RedisCacheConfiguration getConfigWith(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(SerializationPair.fromSerializer(valueSerializer()))
                .entryTtl(ttl);
    }

    private GenericJacksonJsonRedisSerializer valueSerializer() {
        return GenericJacksonJsonRedisSerializer.builder()
                .enableSpringCacheNullValueSupport()   // @Cacheable의 null 결과도 안전하게 캐싱
                .enableUnsafeDefaultTyping()            // 타입정보(@class) 포함
                .build();
    }
}
