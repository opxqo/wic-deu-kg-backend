package com.wic.edu.kg.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RESTful API 统一响应类
 * 
 * 遵循 RESTful 规范：
 * - 成功响应直接返回数据，HTTP 状态码表示结果
 * - 错误响应返回错误详情，HTTP 状态码表示错误类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API响应")
public class ApiResponse<T> {

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "错误码（仅错误时返回）", example = "VALIDATION_ERROR")
    private String error;

    @Schema(description = "错误消息（仅错误时返回）", example = "邮箱格式不正确")
    private String message;

    @Schema(description = "请求路径（仅错误时返回）", example = "/api/users")
    private String path;

    @Schema(description = "时间戳（仅错误时返回）")
    private LocalDateTime timestamp;

    // ==================== 成功响应 ====================

    /**
     * 无数据成功响应（用于 DELETE 等操作）
     */
    public static <T> ApiResponse<T> ok() {
        return ApiResponse.<T>builder().build();
    }

    /**
     * 带数据成功响应
     */
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .build();
    }

    /**
     * 创建成功响应（用于 POST 操作，返回 201）
     */
    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .build();
    }

    // ==================== 错误响应 ====================

    /**
     * 错误响应
     */
    public static <T> ApiResponse<T> error(String error, String message, String path) {
        return ApiResponse.<T>builder()
                .error(error)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
