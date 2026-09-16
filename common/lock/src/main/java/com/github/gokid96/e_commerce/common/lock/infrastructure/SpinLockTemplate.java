package com.github.gokid96.e_commerce.common.lock.infrastructure;

import com.github.gokid96.e_commerce.common.lock.DefaultLockTemplate;
import com.github.gokid96.e_commerce.common.lock.LockIdHolder;
import com.github.gokid96.e_commerce.common.lock.LockStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpinLockTemplate extends DefaultLockTemplate {

    private static final String UNLOCK_SCRIPT = """
        if redis.call("get", KEYS[1]) == ARGV[1] then
            return redis.call("del", KEYS[1])
        else
            return 0
        end
    """;

    /** 스크립트 SHA 캐시가 재사용되도록 인스턴스를 한 번만 만든다. */
    private static final RedisScript<Long> UNLOCK = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final LockIdHolder lockIdHolder;

    @Override
    public LockStrategy getLockStrategy() {
        return LockStrategy.SPIN_LOCK;
    }

    @Override
    protected void acquireLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit) {
        long startTime = System.currentTimeMillis();
        String lockId = UUID.randomUUID().toString();
        lockIdHolder.set(key, lockId);

        log.debug("락 획득 시도 : {}", key);
        while (!tryLock(key, lockId, leaseTime, timeUnit)) {
            log.debug("락 획득 대기 중 : {}", key);

            if (timeout(startTime, waitTime, timeUnit)) {
                throw new IllegalStateException("락 획득 대기 시간 초과 : " + key);
            }

            Thread.onSpinWait();
        }
    }

    @Override
    protected void releaseLock(String key) {
        if (lockIdHolder.notExists(key)) {
            log.debug("락 해제 생략 : 보유하지 않은 락 : {}", key);
            return;
        }

        // 획득 실패로 진입한 경우에도 lockId 가 남아 있으므로 CAS 스크립트로 안전하게 걸러낸다.
        unlock(key, lockIdHolder.get(key));
        lockIdHolder.remove(key);
        log.debug("락 해제 : {}", key);
    }

    private boolean tryLock(String key, String lockId, long leaseTime, TimeUnit timeUnit) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, lockId, leaseTime, timeUnit));
    }

    private boolean timeout(long startTime, long waitTime, TimeUnit timeUnit) {
        return System.currentTimeMillis() - startTime > timeUnit.toMillis(waitTime);
    }

    private void unlock(String key, String lockId) {
        redisTemplate.execute(UNLOCK, Collections.singletonList(key), lockId);
    }
}
