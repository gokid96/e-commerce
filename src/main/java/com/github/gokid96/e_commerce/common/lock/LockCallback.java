package com.github.gokid96.e_commerce.common.lock;

@FunctionalInterface
public interface LockCallback<T> {

    T doInLock() throws Throwable;

}
