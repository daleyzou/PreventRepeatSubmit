package com.dalelyzou.preventrepeatsubmit.aspect;

import com.dalelyzou.preventrepeatsubmit.constant.RedisKey;
import com.dalelyzou.preventrepeatsubmit.service.LockService;
import com.dalelyzou.preventrepeatsubmit.vo.RsVo;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;

@Aspect
@Component
public class NoRepeatSubmitAspect {
    private static final Logger logger = LoggerFactory.getLogger(NoRepeatSubmitAspect.class);

    private static Gson gson = new Gson();

    private static final String SUFFIX = "SUFFIX";

    @Autowired
    LockService lockService;

    /**
     * 横切点
     */
    @Pointcut("@annotation(noRepeatSubmit)")
    public void repeatPoint(NoRepeatSubmit noRepeatSubmit) {
    }

    /**
     *  接收请求，并记录数据
     */
    @Around(value = "repeatPoint(noRepeatSubmit)")
    public Object doBefore(ProceedingJoinPoint joinPoint, NoRepeatSubmit noRepeatSubmit) {
        String key = RedisKey.NO_REPEAT_LOCK_PREFIX + noRepeatSubmit.location();
        Object[] args = joinPoint.getArgs();
        String name = noRepeatSubmit.name();
        int argIndex = noRepeatSubmit.argIndex();
        String suffix;
        if (StringUtils.isEmpty(name)) {
            suffix = String.valueOf(args[argIndex]);
        } else {
            Map<String, Object> keyAndValue = getKeyAndValue(args[argIndex]);
            Object valueObj = keyAndValue.get(name);
            if (valueObj == null) {
                suffix = SUFFIX;
            } else {
                suffix = String.valueOf(valueObj);
            }
        }
        key = key + ":" + suffix;
        logger.info("==================================================");
        for (Object arg : args) {
            logger.info(gson.toJson(arg));
        }
        logger.info("==================================================");
        int seconds = noRepeatSubmit.seconds();
        logger.info("lock key : " + key);
        if (!lockService.isLock(key, seconds)) {
            return RsVo.fail("操作过于频繁，请稍后重试");
        }
        try {
            Object proceed = joinPoint.proceed();
            return proceed;
        } catch (Throwable throwable) {
            logger.error("运行业务代码出错", throwable);
            throw new RuntimeException(throwable.getMessage());
        } finally {
            lockService.unLock(key);
        }
    }

    public static Map<String, Object> getKeyAndValue(Object obj) {
        Map<String, Object> map = Maps.newHashMap();
        // 得到类对象
        Class userCla = (Class) obj.getClass();
        /* 得到类中的所有属性集合 */
        Field[] fs = userCla.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            // 设置些属性是可以访问的
            f.setAccessible(true);
            Object val = new Object();
            try {
                val = f.get(obj);
                // 得到此属性的值
                // 设置键值
                map.put(f.getName(), val);
            } catch (IllegalArgumentException e) {
                logger.error("getKeyAndValue IllegalArgumentException", e);
            } catch (IllegalAccessException e) {
                logger.error("getKeyAndValue IllegalAccessException", e);
            }

        }
        logger.info("扫描结果：" + gson.toJson(map));
        return map;
    }
}
