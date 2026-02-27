package com.feiya.config;

import com.feiya.interceptor.IpLimitInterceptor;
import com.feiya.interceptor.JwtInterceptor;
import com.feiya.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类（注册JWT+IP限流拦截器）
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final IpLimitInterceptor ipLimitInterceptor; // 新增IP限流拦截器
    private final LogInterceptor logInterceptor; // 新增日志拦截器

    // 构造器注入两个拦截器
    public WebConfig(JwtInterceptor jwtInterceptor, IpLimitInterceptor ipLimitInterceptor,LogInterceptor logInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
        this.ipLimitInterceptor = ipLimitInterceptor;
        this.logInterceptor = logInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 日志拦截器（优先级最高）
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/doc.html",          // Knife4j主页
                        "/webjars/**",        // 文档静态资源
                        "/v3/api-docs/**",    // OpenAPI接口
                        "/swagger-resources/**", // 文档资源
                        // 网站图标（关键：放行favicon.ico）
                        "/favicon.ico",        //放行根路径图标
                        "/api/favicon.ico"     //放行带上下文路径的图标
                );

        // 2. IP限流拦截器（排除文档接口）
        registry.addInterceptor(ipLimitInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/doc.html",          // Knife4j主页
                        "/webjars/**",        // 文档静态资源
                        "/v3/api-docs/**",    // OpenAPI接口
                        "/swagger-resources/**", // 文档资源
                        // 网站图标（关键：放行favicon.ico）
                        "/favicon.ico",        //放行根路径图标
                        "/api/favicon.ico"     //放行带上下文路径的图标
                );

        // 3. JWT拦截器（排除登录/测试/文档接口）
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",        // 登录接口
                        "/user/refresh-token", // 放行续期接口
                        "/hello",             //测试接口
                        "/doc.html",          // Knife4j主页
                        "/webjars/**",        // 文档静态资源
                        "/v3/api-docs/**",    // OpenAPI接口
                        "/swagger-resources/**", // 文档资源
                        // 网站图标（关键：放行favicon.ico）
                        "/favicon.ico",        //放行根路径图标
                        "/api/favicon.ico"     //放行带上下文路径的图标
                );
    }
}