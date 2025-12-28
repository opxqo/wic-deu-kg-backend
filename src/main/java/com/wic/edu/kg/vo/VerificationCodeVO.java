package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 验证码发送响应
 */
@Data
@Builder
@Schema(description = "验证码发送响应 - 包含验证码发送状态和相关时间信息")
public class VerificationCodeVO {

    @Schema(description = "是否发送成功", example = "true")
    private boolean success;

    @Schema(description = "操作结果提示消息", example = "验证码已发送至您的邮箱，请注意查收")
    private String message;

    @Schema(description = "验证码有效时间（秒），超过此时间验证码将失效", example = "600")
    private int expireSeconds;

    @Schema(description = "重发冷却时间（秒），需等待此时间后才能重新发送，0表示可立即发送", example = "60")
    private int cooldownSeconds;

    @Schema(description = "脱敏后的邮箱地址，用于前端显示确认", example = "te***@example.com")
    private String maskedEmail;
}
