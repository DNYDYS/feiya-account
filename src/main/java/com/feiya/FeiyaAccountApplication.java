package com.feiya;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 飞鸭AI记账启动类（驼峰命名规范版）
 * 类名：FeiyaAccountApplication（大驼峰）
 * 包名：com.feiya（小写+点分隔，符合Java规范）
 */
@SpringBootApplication
@MapperScan("com.feiya.mapper") // 扫描Mapper接口（小驼峰包名）
public class FeiyaAccountApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeiyaAccountApplication.class, args);
        System.out.println("======= 飞鸭AI记账极简框架启动成功 =======");
        System.out.println("文档地址：http://localhost:8080/api/doc.html");
    }
}