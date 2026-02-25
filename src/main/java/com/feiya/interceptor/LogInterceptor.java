package com.feiya.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;

/**
 * 接口请求日志拦截器（自动记录请求/响应信息）
 */
@Slf4j // 简化日志调用（无需手动创建Logger）
@Component
public class LogInterceptor implements HandlerInterceptor {

    // 记录请求开始时间
    private static final String REQUEST_START_TIME = "requestStartTime";

    /**
     * 请求处理前：记录请求URL、参数、IP等
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取请求基本信息
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = getRealClientIp(request);
        long startTime = System.currentTimeMillis();

        // 2. 存储开始时间到请求属性
        request.setAttribute(REQUEST_START_TIME, startTime);

        // 3. 记录请求日志（排除静态资源和文档接口）
        if (!isExcludePath(requestUri)) {
            log.info("======= 请求开始 =======");
            log.info("请求URL：{},客户端IP：{}", requestUri, clientIp);
            log.info("请求URL：{},请求头：{}", requestUri, getRequestHeaders(request));
            log.info("请求URL：{},请求参数：{}", requestUri, getRequestParams(request));
        }

        return true;
    }

    /**
     * 请求处理后：记录响应状态、耗时
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String requestUri = request.getRequestURI();
        if (!isExcludePath(requestUri)) {
            // 计算耗时
            long startTime = (long) request.getAttribute(REQUEST_START_TIME);
            long costTime = System.currentTimeMillis() - startTime;

            // 记录响应日志
            log.info("请求URL：{},响应状态码：{}", requestUri, response.getStatus());
            log.info("请求URL：{},请求耗时：{}ms", requestUri, costTime);
            // 接口耗时5到十秒
            if (costTime>=5000 && costTime<10000){
                log.info("接口耗超过5秒，请关注：请求URL：{},请求耗时：{}ms", requestUri,costTime);
            }
            // 接口耗时5到十秒
            if (costTime>= 10000){
                log.info("接口耗时超过10秒，请注意：请求URL：{},请求耗时：{}ms", requestUri,costTime);
            }
            log.info("======= 请求结束 =======\n");
        }
    }

    /**
     * 请求完成后：记录异常（如有）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestUri = request.getRequestURI();
        if (ex != null && !isExcludePath(requestUri)) {
            // 记录异常日志（包含堆栈）
            log.error("接口请求异常：URL={} {}", request.getMethod(), requestUri, ex);
        }
    }

    /**
     * 排除无需记录日志的路径（静态资源、文档、图标）
     */
    private boolean isExcludePath(String requestUri) {
        return requestUri.contains("/webjars/")
                || requestUri.contains("/api/v3/api-docs/")
                || requestUri.contains("/swagger-resources/")
                || requestUri.endsWith("/doc.html")
                || requestUri.endsWith("/favicon.ico")
                || requestUri.endsWith("/api/favicon.ico");
    }

    /**
     * 获取客户端真实IP（复用IP限流拦截器的逻辑）
     */
    private String getRealClientIp(HttpServletRequest request) {
        String[] ipHeaders = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        for (String header : ipHeaders) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.contains(",") ? ip.split(",")[0].trim() : ip;
            }
        }
        return "0:0:0:0:0:0:0:1".equals(request.getRemoteAddr()) ? "127.0.0.1" : request.getRemoteAddr();
    }

    /**
     * 获取请求头信息
     */
    private String getRequestHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            // 排除敏感头（如cookie）
            if (!"cookie".equalsIgnoreCase(name) && !"authorization".equalsIgnoreCase(name)) {
                headers.append(name).append("=").append(request.getHeader(name)).append("; ");
            }
        }
        return headers.length() > 0 ? headers.substring(0, headers.length() - 2) : "无";
    }

    /**
     * 获取请求参数（兼容GET/POST）
     */
    private String getRequestParams(HttpServletRequest request) {
        StringBuilder params = new StringBuilder();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            params.append(name).append("=").append(request.getParameter(name)).append("; ");
        }
        return params.length() > 0 ? params.substring(0, params.length() - 2) : "无";
    }
}