package com.feiya.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试接口（验证项目是否启动）
 */
@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "飞鸭AI记账框架启动成功！";
    }
}