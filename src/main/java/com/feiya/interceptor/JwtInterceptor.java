package com.feiya.interceptor;

import com.feiya.exception.GlobalExceptionHandler;
import com.feiya.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器（极简版）
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    // 构造器注入JwtUtil
    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 排除登录接口（无需认证）
        String requestUri = request.getRequestURI();
        if (requestUri.contains("/user/login")) {
            return true;
        }

        // 2. 获取请求头中的token
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            throw new GlobalExceptionHandler.AuthException("未登录，请先获取token");
        }

        // 3. 验证token
        if (!jwtUtil.validateToken(token)) {
            throw new GlobalExceptionHandler.AuthException("token无效或已过期");
        }

        // 4. 解析用户ID，放入请求属性（后续接口可获取）
        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        return true;
    }
}