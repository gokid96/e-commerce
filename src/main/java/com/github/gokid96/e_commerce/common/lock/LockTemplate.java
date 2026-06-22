package com.github.gokid96.e_commerce.common.lock;

import java.util.concurrent.TimeUnit;

public interface LockTemplate {

    LockStrategy getLockStrategy();

    <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit, LockCallback<T> callback) throws Throwable;
}

