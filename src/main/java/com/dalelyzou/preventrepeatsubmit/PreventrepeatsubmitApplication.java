package com.dalelyzou.preventrepeatsubmit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ServletComponentScan
@EnableCaching
@EnableScheduling
public class PreventrepeatsubmitApplication {

    public static void main(String[] args) {
        SpringApplication.run(PreventrepeatsubmitApplication.class, args);
    }

}
