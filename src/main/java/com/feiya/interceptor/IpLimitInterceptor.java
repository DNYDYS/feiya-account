package com.feiya.interceptor;

import com.feiya.exception.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * IP限流拦截器（原生Java实现，无Hutool依赖）
 */
@Component
public class IpLimitInterceptor implements HandlerInterceptor {

    // 从配置文件读取限流参数
    @Value("${feiya.ip-limit.max-count:200}")
    private int maxCount;

    @Value("${feiya.ip-limit.expire:60}")
    private int expire;

    @Autowired
    RedissonClient redissonClient;

//    // 注入Redisson客户端（自动配置，无需手动创建）
//    private final RedissonClient redissonClient;
//
//    // 构造器注入RedissonClient
//    public IpLimitInterceptor(RedissonClient redissonClient) {
//        this.redissonClient = redissonClient;
//    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 原生Java获取客户端真实IP（兼容代理/Nginx）
        String clientIp = getRealClientIp(request);
        // 2. 构建限流Key（格式：ip_limit:127.0.0.1）
        String limitKey = "ip_limit:" + clientIp;

        // 3. 创建Redisson限流器（基于Redis的分布式限流）
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(limitKey);
        // 配置限流规则：每分钟maxCount次请求
        rateLimiter.trySetRate(RateType.OVERALL, maxCount, expire, RateIntervalUnit.SECONDS);

        // 4. 尝试获取令牌（1个请求=1个令牌）
        boolean acquire = rateLimiter.tryAcquire(1);
        if (!acquire) {
            // 获取失败，抛出限流异常
            throw new GlobalExceptionHandler.AuthException(429, "请求过于频繁，请稍后再试");
        }

        return true;
    }

    /**
     * 原生Java实现：获取客户端真实IP（覆盖所有场景）
     */
    private String getRealClientIp(HttpServletRequest request) {
        // 依次从代理头获取，兜底为远程地址
        String[] ipHeaders = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP"
        };

        for (String header : ipHeaders) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 处理多IP情况（X-Forwarded-For可能返回多个IP，取第一个）
                if (ip.contains(",")) {
                    return ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        // 本地环境处理：IPv6的0:0:0:0:0:0:0:1转为IPv4的127.0.0.1
        String remoteAddr = request.getRemoteAddr();
        return "0:0:0:0:0:0:0:1".equals(remoteAddr) ? "127.0.0.1" : remoteAddr;
    }
}