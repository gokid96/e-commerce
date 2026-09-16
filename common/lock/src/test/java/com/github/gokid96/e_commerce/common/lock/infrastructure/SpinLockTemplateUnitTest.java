package com.github.gokid96.e_commerce.common.lock.infrastructure;

import com.github.gokid96.e_commerce.common.lock.LockCallback;
import com.github.gokid96.e_commerce.common.lock.LockIdHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpinLockTemplateUnitTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    private SpinLockTemplate lockTemplate;
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        // LockIdHolder 는 스레드 로컬 보관만 하므로 실제 구현으로 검증한다.
        lockTemplate = new SpinLockTemplate(redisTemplate, new LockIdHolder());
        valueOperations = mock(ValueOperations.class);
    }

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
        TransactionSynchronizationManager.setActualTransactionActive(false);
    }

    @DisplayName("락을 획득하지 못하면 재시도 후 대기 시간을 초과하여 예외가 발생한다.")
    @Test
    void executeWithLockWhenNotAcquiredLock() {
        givenSetIfAbsent(false);

        assertThatThrownBy(() -> lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("락 획득 대기 시간 초과");

        verify(valueOperations, atLeast(2)).setIfAbsent(any(), any(), anyLong(), any());
    }

    @DisplayName("락을 획득하면 콜백이 실행된다.")
    @Test
    void executeWithLockWhenAcquiredLock() throws Throwable {
        givenSetIfAbsent(true);

        assertThat(lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback())).isEqualTo("callback");
    }

    @DisplayName("락을 획득하지 못하면 재시도를 통해 락을 획득한다.")
    @Test
    void executeWithLockWhenRetry() throws Throwable {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(any(), any(), anyLong(), any())).thenReturn(false, true);

        assertThat(lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback())).isEqualTo("callback");
    }

    @DisplayName("락을 획득하면 콜백이 실행되고, 락을 해제한다.")
    @Test
    void executeWithLockAfterUnlock() throws Throwable {
        givenSetIfAbsent(true);

        lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback());

        verify(redisTemplate, times(1)).execute(any(RedisScript.class), eq(Collections.singletonList("key")), any());
    }

    @DisplayName("트랜잭션이 열려 있으면 커밋이 끝난 뒤에 락을 해제한다.")
    @Test
    void executeWithLockReleasesAfterTransactionCompletion() throws Throwable {
        givenSetIfAbsent(true);
        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(true);

        lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback());

        // 콜백이 끝난 시점에는 아직 해제되지 않는다.
        verify(redisTemplate, never()).execute(any(RedisScript.class), any(), any());

        List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
        assertThat(synchronizations).hasSize(1);
        synchronizations.forEach(it -> it.afterCompletion(TransactionSynchronization.STATUS_COMMITTED));

        verify(redisTemplate, times(1)).execute(any(RedisScript.class), eq(Collections.singletonList("key")), any());
    }

    @DisplayName("같은 키의 락을 중첩해서 획득하면 예외가 발생한다.")
    @Test
    void executeWithLockWhenReentrant() {
        givenSetIfAbsent(true);

        LockCallback<String> nested = () ->
                lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback());

        assertThatThrownBy(() -> lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, nested))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 동일한 키에 대한 락을 보유 중입니다");
    }

    private void givenSetIfAbsent(boolean acquired) {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(any(), any(), anyLong(), any())).thenReturn(acquired);
    }

    private LockCallback<String> callback() {
        return () -> "callback";
    }
}
