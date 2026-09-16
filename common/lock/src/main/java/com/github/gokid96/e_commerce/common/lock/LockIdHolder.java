package com.github.gokid96.e_commerce.common.lock;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 스레드가 보유한 락 식별자를 기억한다.
 *
 * <p>획득과 해제가 분리되면서 해제 시점에 "내가 이 락을 들고 있는지"를 알 수 없게 되므로,
 * 스레드별로 {@code key -> lockId} 를 들고 있는다. 보유하지 않은 락에 대한 해제 시도와
 * 같은 키의 중복 획득을 여기서 걸러낸다.
 */
@Component
public class LockIdHolder {

    private final ThreadLocal<Map<String, String>> holder = ThreadLocal.withInitial(HashMap::new);

    public void set(String key, String lockId) {
        if (exists(key)) {
            throw new IllegalStateException("이미 동일한 키에 대한 락을 보유 중입니다 : " + key);
        }
        holder.get().put(key, lockId);
    }

    public String get(String key) {
        return holder.get().get(key);
    }

    public boolean notExists(String key) {
        return !exists(key);
    }

    public void remove(String key) {
        Map<String, String> lockIds = holder.get();
        lockIds.remove(key);

        // 스레드 풀에서 재사용되는 스레드에 빈 맵이 남지 않도록 정리한다.
        if (lockIds.isEmpty()) {
            holder.remove();
        }
    }

    private boolean exists(String key) {
        return holder.get().get(key) != null;
    }
}
