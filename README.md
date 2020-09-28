# PreventRepeatSubmit
 基于注解和redis的分布式锁实现防重复提交
 
 ## 背景
 同一条数据被用户点击了多次，导致数据冗余，需要防止弱网络等环境下的重复点击
 
 ## 目标
 通过在指定的接口处添加注解，实现根据指定的接口参数来防重复点击
 
 ## 说明
 这里的重复点击是指在指定的时间段内多次点击按钮
 
 ## 技术方案
 springboot + redis锁 + 注解
 
 使用 feign client 进行请求测试
 
 ## 最终的使用实例
 1、根据接口收到 PathVariable 参数判断唯一
 
 ```
 /**
      *  根据请求参数里的 PathVariable 里获取的变量进行接口级别防重复点击
      *
      * @param testId 测试id
      * @param requestVo 请求参数
      * @return
      * @author daleyzou
      */
     @PostMapping("/test/{testId}")
     @NoRepeatSubmit(location = "thisIsTestLocation", seconds = 6)
     public RsVo thisIsTestLocation(@PathVariable Integer testId, @RequestBody RequestVo requestVo) throws Throwable {
         // 睡眠 5 秒，模拟业务逻辑
         Thread.sleep(5);
         return RsVo.success("test is return success");
     }
 ```
 
 
 2、根据接口收到的 RequestBody 中指定变量名的值判断唯一
 
 ```
 /**
      *  根据请求参数里的 RequestBody 里获取指定名称的变量param5的值进行接口级别防重复点击
      *
      * @param testId 测试id
      * @param requestVo 请求参数
      * @return
      * @author daleyzou
      */
     @PostMapping("/test/{testId}")
     @NoRepeatSubmit(location = "thisIsTestBody", seconds = 6, argIndex = 1, name = "param5")
     public RsVo thisIsTestBody(@PathVariable Integer testId, @RequestBody RequestVo requestVo) throws Throwable {
         // 睡眠 5 秒，模拟业务逻辑
         Thread.sleep(5);
         return RsVo.success("test is return success");
     }
 ```
 
 ps: jedis 2.9 和 springboot有各种兼容问题，无奈只有降低springboot的版本了
 
 
 ## 运行结果
 
 ```
 收到响应：{"succeeded":true,"code":500,"msg":"操作过于频繁，请稍后重试","data":null}
 收到响应：{"succeeded":true,"code":500,"msg":"操作过于频繁，请稍后重试","data":null}
 收到响应：{"succeeded":true,"code":500,"msg":"操作过于频繁，请稍后重试","data":null}
 收到响应：{"succeeded":true,"code":200,"msg":"success","data":"test is return success"}
 ```
 
 ## 测试用例
 
 ```
 package com.dalelyzou.preventrepeatsubmit.controller;
 
 import com.dalelyzou.preventrepeatsubmit.PreventrepeatsubmitApplicationTests;
 import com.dalelyzou.preventrepeatsubmit.service.AsyncFeginService;
 import com.dalelyzou.preventrepeatsubmit.vo.RequestVo;
 import org.junit.jupiter.api.Test;
 import org.springframework.beans.factory.annotation.Autowired;
 
 import java.io.IOException;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 
 /**
  * TestControllerTest
  * @description 防重复点击测试类
  * @author daleyzou
  * @date 2020年09月28日 17:13
  * @version 1.3.1
  */
 class TestControllerTest extends PreventrepeatsubmitApplicationTests {
     @Autowired
     AsyncFeginService asyncFeginService;
 
     @Test
     public void thisIsTestLocation() throws IOException {
         RequestVo requestVo = new RequestVo();
         requestVo.setParam5("random");
         ExecutorService executorService = Executors.newFixedThreadPool(4);
         for (int i = 0; i <= 3; i++) {
             executorService.execute(() -> {
                 String kl = asyncFeginService.thisIsTestLocation(requestVo);
                 System.err.println("收到响应：" + kl);
             });
         }
         System.in.read();
     }
 
     @Test
     public void thisIsTestBody() throws IOException {
         RequestVo requestVo = new RequestVo();
         requestVo.setParam5("special");
         ExecutorService executorService = Executors.newFixedThreadPool(4);
         for (int i = 0; i <= 3; i++) {
             executorService.execute(() -> {
                 String kl = asyncFeginService.thisIsTestBody(requestVo);
                 System.err.println("收到响应：" + kl);
             });
         }
         System.in.read();
     }
 }
 
 ```
 
 ## 定义一个注解
 
 ```
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
 
 ```
 
 ## 根据指定的注解定义一个切面，根据参数中的指定值来判断请求是否重复
 
 ```
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
 
 ```
 
## 项目完整代码
https://github.com/daleyzou/PreventRepeatSubmit
