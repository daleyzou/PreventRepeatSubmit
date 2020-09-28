package com.dalelyzou.preventrepeatsubmit.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * NoRepeatSubmit
 * @description 重复点击的切面
 * @author daleyzou
 * @date 2020年09月23日 14:35
 * @version 1.4.8
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRepeatSubmit {
    /**
     * 锁过期的时间
     * */
    int seconds() default 5;
    /**
     * 锁的位置
     * */
    String location() default "NoRepeatSubmit";
    /**
     * 要扫描的参数位置
     * */
    int argIndex() default 0;
    /**
     * 参数名称
     * */
    String name() default "";
}
