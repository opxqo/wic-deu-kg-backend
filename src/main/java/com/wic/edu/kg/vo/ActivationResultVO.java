package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 账号激活响应
 */
@Data
@Builder
@Schema(description = "账号激活响应 - 包含激活结果和用户基本信息")
public class ActivationResultVO {

    @Schema(description = "是否激活成功", example = "true")
    private boolean success;

    @Schema(description = "操作结果提示消息", example = "账号激活成功，欢迎使用武汉城市学院教务服务平台！")
    private String message;

    @Schema(description = "用户名（登录时使用）", example = "zhangsan")
    private String username;

    @Schema(description = "学号", example = "202012345678")
    private String studentId;

    @Schema(description = "注册邮箱", example = "student@example.com")
    private String email;

    @Schema(description = "账号激活时间", example = "2025-12-28T00:30:00")
    private LocalDateTime activatedAt;
}
