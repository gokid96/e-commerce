// CacheType별 캐시이름으로 "name*" 키를 찾아 삭제. (flushDb 대신 캐시 키만)
package com.github.gokid96.e_commerce.support.database;

import com.github.gokid96.e_commerce.common.cache.CacheType;
import com.github.gokid96.e_commerce.common.cache.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Set;

@Component
@Profile("test")
public class RedisCacheCleaner {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisCacheCleaner(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void clean() {
        Arrays.stream(CacheType.values())
                .map(Cacheable::cacheName)
                .map(this::getKeys)
                .filter(keys -> !CollectionUtils.isEmpty(keys))
                .forEach(stringRedisTemplate::delete);
    }

    private Set<String> getKeys(String cacheName) {
        return stringRedisTemplate.keys(cacheName + "*");
    }
}
