package com.github.gokid96.e_commerce.common.lock;

import org.springframework.transaction.support.TransactionSynchronization;

import java.util.concurrent.TimeUnit;

import static org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive;
import static org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization;

/**
 * 락 획득/해제의 공통 흐름을 담는다.
 *
 * <p>트랜잭션이 열려 있으면 해제를 {@code afterCompletion} 으로 미룬다.
 * 콜백 안의 트랜잭션이 커밋되기 전에 락을 풀면, 다음 스레드가 락을 잡고도
 * 아직 반영되지 않은 값을 읽어 상호 배제가 무의미해진다.
 */
public abstract class DefaultLockTemplate implements LockTemplate {

    @Override
    public <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit, LockCallback<T> callback) throws Throwable {
        try {
            acquireLock(key, waitTime, leaseTime, timeUnit);
            return callback.doInLock();
        } finally {
            releaseAfterTransaction(key);
        }
    }

    private void releaseAfterTransaction(String key) {
        if (!isActualTransactionActive()) {
            releaseLock(key);
            return;
        }

        registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                releaseLock(key);
            }
        });
    }

    protected abstract void acquireLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException;

    protected abstract void releaseLock(String key);
}
