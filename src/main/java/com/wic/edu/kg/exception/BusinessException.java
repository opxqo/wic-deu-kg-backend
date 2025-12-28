package com.wic.edu.kg.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 业务异常类（RESTful 风格）
 * 
 * 支持标准 HTTP 状态码映射
 */
@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;

    /**
     * 通用错误（500）
     */
    public BusinessException(String message) {
        super(message);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errorCode = "INTERNAL_ERROR";
    }

    /**
     * 指定 HTTP 状态码的错误
     */
    public BusinessException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = httpStatus.name();
    }

    /**
     * 完整构造（状态码 + 错误码 + 消息）
     */
    public BusinessException(HttpStatus httpStatus, String errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    /**
     * 带原因的异常
     */
    public BusinessException(HttpStatus httpStatus, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    // ==================== 常用工厂方法 ====================

    /**
     * 400 Bad Request - 参数错误
     */
    public static BusinessException badRequest(String message) {
        return new BusinessException(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message);
    }

    /**
     * 401 Unauthorized - 未认证
     */
    public static BusinessException unauthorized(String message) {
        return new BusinessException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message);
    }

    /**
     * 403 Forbidden - 无权限
     */
    public static BusinessException forbidden(String message) {
        return new BusinessException(HttpStatus.FORBIDDEN, "FORBIDDEN", message);
    }

    /**
     * 404 Not Found - 资源不存在
     */
    public static BusinessException notFound(String message) {
        return new BusinessException(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }

    /**
     * 409 Conflict - 资源冲突
     */
    public static BusinessException conflict(String message) {
        return new BusinessException(HttpStatus.CONFLICT, "CONFLICT", message);
    }

    /**
     * 429 Too Many Requests - 请求过于频繁
     */
    public static BusinessException tooManyRequests(String message) {
        return new BusinessException(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", message);
    }

    /**
     * 500 Internal Server Error - 服务器内部错误
     */
    public static BusinessException internal(String message) {
        return new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", message);
    }

    /**
     * 兼容旧代码的构造方法
     * 
     * @deprecated 请使用新的工厂方法
     */
    @Deprecated
    public BusinessException(Integer code, String message) {
        super(message);
        this.httpStatus = mapCodeToStatus(code);
        this.errorCode = this.httpStatus.name();
    }

    private static HttpStatus mapCodeToStatus(Integer code) {
        return switch (code) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 403 -> HttpStatus.FORBIDDEN;
            case 404 -> HttpStatus.NOT_FOUND;
            case 409 -> HttpStatus.CONFLICT;
            case 429 -> HttpStatus.TOO_MANY_REQUESTS;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    /**
     * 获取 HTTP 状态码（兼容旧代码）
     */
    public Integer getCode() {
        return httpStatus.value();
    }
}
