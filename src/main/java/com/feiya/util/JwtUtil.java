package com.feiya.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JWT工具类（新增续期功能）
 */
@Component
public class JwtUtil {

    // 密钥（建议配置在application.yml，此处简化）
    @Value("${jwt.secret:feiya-ai-account-secret-key-2026}")
    private String secret;

    // 访问令牌过期时间：2小时（7200秒）
    @Value("${jwt.access-token-expire:7200000}")
    private long accessTokenExpire;

    // 刷新令牌过期时间：7天（可选）
    @Value("${jwt.refresh-token-expire:604800000}")
    private long refreshTokenExpire;

    /**
     * 生成访问令牌（原有方法）
     */
    public String generateAccessToken(Long userId) {
        return generateToken(userId, accessTokenExpire);
    }

    /**
     * 生成刷新令牌（新增）
     */
    public String generateRefreshToken(Long userId) {
        return generateToken(userId, refreshTokenExpire);
    }

    /**
     * 通用生成令牌方法
     */
    private String generateToken(Long userId, long expireTime) {
        // 1. 构建密钥
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        // 2. 构建Claims（载荷）
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        // 3. 生成令牌
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(key)
                .compact();
    }

    /**
     * 验证令牌有效性（原有方法，新增过期校验）
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            // 校验令牌是否过期
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析令牌获取用户ID（原有方法）
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.get("userId").toString());
    }

    /**
     * 令牌续期核心方法（新增）
     * @param oldToken 旧令牌
     * @return 新的访问令牌
     */
    public String refreshAccessToken(String oldToken) {
        // 1. 验证旧令牌有效性
        if (!validateToken(oldToken)) {
            throw new RuntimeException("令牌已过期或无效，无法续期");
        }

        // 2. 解析旧令牌获取用户ID
        Long userId = getUserIdFromToken(oldToken);

        // 3. 生成新的访问令牌
        return generateAccessToken(userId);
    }

    /**
     * 解析令牌（私有方法）
     */
    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 新增Redis存储刷新令牌，支持拉黑
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 登录时存储刷新令牌
    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = "refresh_token:" + userId;
        redisTemplate.opsForValue().set(key, refreshToken, refreshTokenExpire, TimeUnit.MILLISECONDS);
    }

    // 续期时校验刷新令牌
    public boolean validateRefreshToken(Long userId, String refreshToken) {
        String key = "refresh_token:" + userId;
        String storedToken = (String) redisTemplate.opsForValue().get(key);
        return refreshToken.equals(storedToken);
    }

    // 退出登录时删除刷新令牌
    public void deleteRefreshToken(Long userId) {
        String key = "refresh_token:" + userId;
        redisTemplate.delete(key);
    }
}