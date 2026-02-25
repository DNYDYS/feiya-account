package com.feiya.controller;

import com.feiya.dto.LoginRequestDTO;
import com.feiya.exception.GlobalExceptionHandler;
import com.feiya.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j; // 新增：简化日志调用
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 用户控制器（添加业务日志）
 */
@Slf4j // 新增：自动创建Logger对象
@RestController
@RequestMapping("/user")
@Tag(name = "用户模块", description = "用户登录、信息查询接口")
public class UserController {

//    private final JwtUtil jwtUtil;
//
//    public UserController(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "传入JSON格式的code获取JWT令牌，测试code=123456")
    public GlobalExceptionHandler.R<String> login(@RequestBody LoginRequestDTO loginRequest) {
        String code = loginRequest.getCode();
        log.info("用户登录请求，code：{}", code); // 业务日志

        if ("123456".equals(code)) {
            String token = jwtUtil.generateToken(1L);
            redisTemplate.opsForValue().set("test:TOKEN", token,300 , TimeUnit.SECONDS);
            log.info("用户登录成功，生成token：{}", token); // 成功日志
            return GlobalExceptionHandler.R.success(token);
        } else {
            log.warn("用户登录失败，无效code：{}", code); // 警告日志
            return GlobalExceptionHandler.R.error(400, "code无效");
        }
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "需在请求头传入token，示例：token=xxx")
    public GlobalExceptionHandler.R<String> getUserInfo(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询用户信息，用户ID：{}", userId); // 业务日志
        return GlobalExceptionHandler.R.success("用户ID：" + userId + "，认证成功！");
    }
}