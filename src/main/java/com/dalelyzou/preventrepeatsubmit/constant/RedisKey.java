package com.dalelyzou.preventrepeatsubmit.constant;

/**
 * RedisKey
 * @description 锁前缀类
 * @author daleyzou
 * @date 2020年09月28日 16:47
 * @version 1.3.1
 */
public class RedisKey {
    /**
     *  不可重复点击的锁前缀
     * */
    public static final String NO_REPEAT_LOCK_PREFIX = "iflow:no_repeat_lock:";
}
