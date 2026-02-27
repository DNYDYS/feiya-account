package com.feiya.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data // Lombok注解：自动生成getter/setter/toString等方法
@Schema(description = "用户登录请求参数") // Knife4j注解：接口文档说明
public class TokenVO {
    @Schema(description = "访问令牌", required = false)
    private String accessToken; // 访问令牌（2小时过期）
    @Schema(description = "刷新令牌", required = false)
    private String refreshToken; // 刷新令牌（7天过期）
}
