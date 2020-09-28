package com.dalelyzou.preventrepeatsubmit.service;

import com.dalelyzou.preventrepeatsubmit.vo.RequestVo;
import com.dalelyzou.preventrepeatsubmit.vo.RsVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * AsyncFeginService
 * @description TODO
 * @author daleyzou
 * @date 2020年08月14日 21:59
 * @version 1.3.1
 */
@FeignClient(url = "localhost:8080", name = "async")
public interface AsyncFeginService {
    @PostMapping("/test/1")
    String thisIsTestLocation(RequestVo requestVo);

    @PostMapping("/test/body/read")
    String thisIsTestBody(RequestVo requestVo);
}
