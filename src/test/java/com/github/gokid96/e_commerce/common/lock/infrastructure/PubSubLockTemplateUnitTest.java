package com.github.gokid96.e_commerce.common.lock.infrastructure;

import com.github.gokid96.e_commerce.common.lock.LockCallback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PubSubLockTemplateUnitTest {

    @InjectMocks
    private PubSubLockTemplate lockTemplate;

    @Mock
    private RedissonClient redissonClient;

    @DisplayName("락을 획득하지 못하면 예외가 발생한다.")
    @Test
    void executeWithLockWhenNotAcquired() throws InterruptedException {
        RLock lock = mock(RLock.class);
        LockCallback<String> callback = () -> "callback";

        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(false);

        assertThatThrownBy(() -> lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("락 획득 실패");
    }

    @DisplayName("락을 획득하면 콜백을 실행한다.")
    @Test
    void executeWithLockWhenAcquired() throws Throwable {
        RLock lock = mock(RLock.class);
        LockCallback<String> callback = () -> "callback";

        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);

        String result = lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback);

        assertThat(result).isEqualTo("callback");
    }

    @DisplayName("락을 해제한다.")
    @Test
    void executeWithLockAfterUnlock() throws Throwable {
        RLock lock = mock(RLock.class);
        LockCallback<String> callback = () -> "callback";

        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);

        lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback);

        verify(lock, times(1)).unlock();
    }

    @DisplayName("락 해제시, 현재 스레드가 락을 보유하고 있지 않으면 아무것도 하지 않는다.")
    @Test
    void executeWithLockWhenNotHeldByCurrentThread() throws Throwable {
        RLock lock = mock(RLock.class);
        LockCallback<String> callback = () -> "callback";

        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(false);

        lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback);

        verify(lock, never()).unlock();
    }
}
