package com.feiya.controller;

import com.feiya.dto.LoginRequestDTO;
import com.feiya.entity.AiCharacterConfig;
import com.feiya.exception.GlobalExceptionHandler;
import com.feiya.service.AiCharacterConfigService;
import com.feiya.util.JwtUtil;
import com.feiya.vo.TokenVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AiCharacterConfigService aiCharacterConfigService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "传入JSON格式的code获取JWT令牌，测试code=123456")
    public GlobalExceptionHandler.R<TokenVO> login(@RequestBody LoginRequestDTO loginRequest) {
        String code = loginRequest.getCode();
        log.info("用户登录请求，code：{}", code); // 业务日志

        if ("123456".equals(code)) {
            Long userId = 1L;
            // 生成访问令牌+刷新令牌
            String accessToken = jwtUtil.generateAccessToken(userId);
            String refreshToken = jwtUtil.generateRefreshToken(userId);
            log.info("用户登录成功，生成accessToken：{}, refreshToken：{}", accessToken, refreshToken);

            TokenVO tokenVO = new TokenVO();
            tokenVO.setAccessToken(accessToken);
            tokenVO.setRefreshToken(refreshToken);
            return GlobalExceptionHandler.R.success(tokenVO);
        } else {
            log.warn("用户登录失败，无效code：{}", code); // 警告日志
            return GlobalExceptionHandler.R.error(400, "code无效");
        }
    }

    // 新增续期接口
    @PostMapping("/refresh-token")
    @Operation(summary = "JWT令牌续期", description = "传入旧令牌，获取新令牌（需登录态）")
    public GlobalExceptionHandler.R<TokenVO> refreshToken(
            @Parameter(description = "旧的访问令牌", required = true)
            @RequestHeader("token") String oldToken) {
        try {
            log.info("用户令牌续期请求，oldToken：{}", oldToken.substring(0, 20) + "...");
            // 续期生成新令牌
            String newAccessToken = jwtUtil.refreshAccessToken(oldToken);
            // 可选：生成新的刷新令牌
            String newRefreshToken = jwtUtil.generateRefreshToken(jwtUtil.getUserIdFromToken(oldToken));
            log.info("令牌续期成功，新accessToken：{}", newAccessToken.substring(0, 20) + "...");

            TokenVO tokenVO = new TokenVO();
            tokenVO.setAccessToken(newAccessToken);
            tokenVO.setRefreshToken(newRefreshToken);
            return GlobalExceptionHandler.R.success(tokenVO);
        } catch (Exception e) {
            log.error("令牌续期失败", e);
            return GlobalExceptionHandler.R.error(401, e.getMessage());
        }
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "需在请求头传入token，示例：token=xxx")
    public GlobalExceptionHandler.R<String> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询用户信息，用户ID：{}", userId); // 业务日志
        return GlobalExceptionHandler.R.success("用户ID：" + userId + "，认证成功！");
    }

    // 新增续期接口
    @PostMapping("/getByCharacterId")
    @Operation(summary = "根据人设唯一标识获取信息", description = "根据人设唯一标识获取信息")
    public GlobalExceptionHandler.R<AiCharacterConfig> getByCharacterId(
            @Parameter(description = "人设唯一标识", required = true)
            @RequestHeader("characterId") String characterId) {
        AiCharacterConfig byCharacterId = aiCharacterConfigService.getByCharacterId(characterId);
        return GlobalExceptionHandler.R.success(byCharacterId);
    }

}