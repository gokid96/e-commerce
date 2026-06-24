package com.github.gokid96.e_commerce.common.cache;

import java.time.Duration;

public interface Cacheable {

    String createKey(String key);

    String cacheName();

    Duration ttl();
}
