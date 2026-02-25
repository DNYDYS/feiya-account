package com.feiya.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户登录请求DTO（专门接收JSON格式的登录参数）
 */
@Data // Lombok注解：自动生成getter/setter/toString等方法
@Schema(description = "用户登录请求参数") // Knife4j注解：接口文档说明
public class LoginRequestDTO {
    @Schema(description = "微信登录code", required = true, example = "123456") // 接口文档中参数的说明
    private String code; // 登录接口的核心参数（对应JSON中的code字段）

    // 新增账号密码字段
    @Schema(description = "用户名", required = false, example = "zhangsan")
    private String username;

    @Schema(description = "密码", required = false, example = "123456")
    private String password;

}