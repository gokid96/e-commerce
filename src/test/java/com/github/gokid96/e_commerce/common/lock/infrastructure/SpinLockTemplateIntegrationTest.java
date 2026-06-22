package com.github.gokid96.e_commerce.common.lock.infrastructure;

import com.github.gokid96.e_commerce.common.lock.LockCallback;
import com.github.gokid96.e_commerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SpinLockTemplateIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private SpinLockTemplate lockTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.delete("lock:spinLockTest");
    }

    @DisplayName("락을 획득하지 못하면 재시도 후 대기 시간을 초과하여 예외가 발생한다.")
    @Test
    void executeWithLockWhenNotAcquiredLock() {
        LockCallback<String> callback = () -> "callback";
        redisTemplate.opsForValue().setIfAbsent("lock:spinLockTest", "lockId", 10, TimeUnit.MINUTES);

        assertThatThrownBy(() -> lockTemplate.executeWithLock("lock:spinLockTest", 1L, 1L, TimeUnit.SECONDS, callback))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("락 획득 대기 시간 초과");

    }

    @DisplayName("락을 획득하면 콜백이 실행된다.")
    @Test
    void executeWithLockWhenAcquiredLock() throws Throwable {
        LockCallback<String> callback = () -> "callback";

        String result = lockTemplate.executeWithLock("lock:spinLockTest", 1L, 1L, TimeUnit.SECONDS, callback);

        assertThat(result).isEqualTo("callback");
    }

    @DisplayName("락을 획득하면 콜백이 실행되고, 락이 해제된다.")
    @Test
    void executeWithLockWhenAcquiredLockAndRelease() throws Throwable {
        LockCallback<String> callback = () -> "callback";

        lockTemplate.executeWithLock("lock:spinLockTest", 1L, 1L, TimeUnit.SECONDS, callback);

        assertThat(redisTemplate.hasKey("lock:spinLockTest")).isFalse();
    }

}
