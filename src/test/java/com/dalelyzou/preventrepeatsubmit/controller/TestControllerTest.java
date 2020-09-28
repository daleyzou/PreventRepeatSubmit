package com.dalelyzou.preventrepeatsubmit.controller;

import com.dalelyzou.preventrepeatsubmit.PreventrepeatsubmitApplicationTests;
import com.dalelyzou.preventrepeatsubmit.service.AsyncFeginService;
import com.dalelyzou.preventrepeatsubmit.vo.RequestVo;
import org.junit.Test;
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
    void thisIsTestLocation() throws IOException {
        RequestVo requestVo = new RequestVo();
        requestVo.setParam1("random");
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
    void thisIsTestBody() {
        RequestVo requestVo = new RequestVo();
        requestVo.setParam5("special");
        String rsVo = asyncFeginService.thisIsTestBody(requestVo);
        System.out.println(rsVo);
    }
}