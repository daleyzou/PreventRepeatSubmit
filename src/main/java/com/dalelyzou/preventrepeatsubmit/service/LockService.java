package com.dalelyzou.preventrepeatsubmit.service;

public interface LockService {

    void unLock(String key);

    boolean isLock(String key, int seconds);

    void unLock(String key, String value);

    <T> T lockExecute(String key, LockExecute<T> lockExecute);

    interface LockExecute<T> {
        T execute();

        T waitTimeOut();
    }
}
