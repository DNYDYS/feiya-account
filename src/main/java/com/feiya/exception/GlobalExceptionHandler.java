package com.feiya.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理（极简版）
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthException.class)
    public R<?> handleAuthException(AuthException e) {
        return R.error(401, e.getMessage());
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        return R.error(500, "服务器内部错误：" + e.getMessage());
    }

    // 统一返回结果封装
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class R<T> {
        private int code; // 200成功，401未认证，500异常
        private String msg;
        private T data;

        // 成功响应
        public static <T> R<T> success(T data) {
            return new R<>(200, "操作成功", data);
        }

        // 失败响应
        public static <T> R<T> error(int code, String msg) {
            return new R<>(code, msg, null);
        }
    }

    // 自定义认证异常
    public static class AuthException extends RuntimeException {
        private int code; // 新增code字段

        public AuthException(String message) {
            super(message);
            this.code = 401; // 默认code=401
        }
        // 新增：支持传入code和message的构造方法
        public AuthException(int code, String message) {
            super(message);
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}