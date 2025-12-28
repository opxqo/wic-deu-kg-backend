package com.wic.edu.kg.exception;

import com.wic.edu.kg.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * RESTful 全局异常处理器
 * 
 * 使用标准 HTTP 状态码：
 * - 400 Bad Request: 参数错误
 * - 401 Unauthorized: 未认证
 * - 403 Forbidden: 无权限
 * - 404 Not Found: 资源不存在
 * - 409 Conflict: 资源冲突
 * - 429 Too Many Requests: 请求过于频繁
 * - 500 Internal Server Error: 服务器错误
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== 业务异常 ====================

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {} - {}", e.getErrorCode(), e.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                e.getErrorCode(),
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    // ==================== 认证授权异常 ====================

    /**
     * 处理认证异常（401）
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e,
            HttpServletRequest request) {
        log.warn("认证失败: {}", e.getMessage());

        String message = e instanceof BadCredentialsException ? "用户名或密码错误" : "认证失败，请重新登录";

        ApiResponse<Void> response = ApiResponse.error(
                "UNAUTHORIZED",
                message,
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 处理访问拒绝异常（403）
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e,
            HttpServletRequest request) {
        log.warn("访问拒绝: {}", e.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                "FORBIDDEN",
                "权限不足，无法访问该资源",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // ==================== 参数校验异常 (400) ====================

    /**
     * 处理参数校验异常（@RequestBody）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e,
            HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("参数校验失败: {}", message);

        ApiResponse<Void> response = ApiResponse.error(
                "VALIDATION_ERROR",
                message,
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理参数校验异常（@RequestParam）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException e,
            HttpServletRequest request) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        log.warn("参数校验失败: {}", message);

        ApiResponse<Void> response = ApiResponse.error(
                "VALIDATION_ERROR",
                message,
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("参数绑定失败: {}", message);

        ApiResponse<Void> response = ApiResponse.error(
                "BINDING_ERROR",
                message,
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameterException(MissingServletRequestParameterException e,
            HttpServletRequest request) {
        log.warn("缺少请求参数: {}", e.getParameterName());

        ApiResponse<Void> response = ApiResponse.error(
                "MISSING_PARAMETER",
                "缺少必需参数: " + e.getParameterName(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理参数类型转换异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {
        log.warn("参数类型错误: {} - {}", e.getName(), e.getValue());

        ApiResponse<Void> response = ApiResponse.error(
                "TYPE_MISMATCH",
                "参数类型错误: " + e.getName(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ==================== 资源异常 (404) ====================

    /**
     * 处理资源不存在异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException e,
            HttpServletRequest request) {
        log.warn("资源不存在: {}", e.getRequestURL());

        ApiResponse<Void> response = ApiResponse.error(
                "NOT_FOUND",
                "请求的资源不存在",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ==================== 文件上传异常 ====================

    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e,
            HttpServletRequest request) {
        log.warn("文件大小超限: {}", e.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                "FILE_TOO_LARGE",
                "上传文件大小超过限制",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }

    // ==================== 通用异常 (500) ====================

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: ", e);

        ApiResponse<Void> response = ApiResponse.error(
                "INTERNAL_ERROR",
                "系统繁忙，请稍后重试",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
