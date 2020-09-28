package com.dalelyzou.preventrepeatsubmit.controller;

import com.dalelyzou.preventrepeatsubmit.aspect.NoRepeatSubmit;
import com.dalelyzou.preventrepeatsubmit.vo.RsVo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController
 * @description 测试类
 * @author daleyzou
 * @date 2020年09月28日 16:56
 * @version 1.3.1
 */
@RestController
public class TestController {
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
}
