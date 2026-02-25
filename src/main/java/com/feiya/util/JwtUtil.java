package com.feiya.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts; // 核心 Jwts（0.12.3 正确导包）
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${feiya.jwt.secret:feiyaAccount2026#SecretKey123456}")
    private String jwtSecret;

    @Value("${feiya.jwt.expire:86400000}")
    private long expireMillis;

    // 生成token
    public String generateToken(Long userId) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(userId.toString()) // 替代 setSubject，0.12.3 简化写法
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireMillis))
                .signWith(secretKey)
                .compact();
    }

    // 验证token
    public boolean validateToken(String token) {
        try {
            parseTokenClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 解析用户ID
    public Long getUserIdFromToken(String token) {
        Claims claims = parseTokenClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    // 核心解析方法（0.12.3 标准写法）
    private Claims parseTokenClaims(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser() // 0.12.3 用 parser() 而非 parserBuilder()！
                .verifyWith(secretKey) // 替代 setSigningKey，0.12.3 新写法
                .build()
                .parseSignedClaims(token) // 替代 parseClaimsJws，0.12.3 新写法
                .getPayload(); // 替代 getBody，0.12.3 新写法
    }
}