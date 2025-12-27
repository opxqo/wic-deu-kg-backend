package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 验证码发送响应
 */
@Data
@Builder
@Schema(description = "验证码发送响应")
public class VerificationCodeVO {

    @Schema(description = "是否发送成功")
    private boolean success;

    @Schema(description = "提示消息")
    private String message;

    @Schema(description = "验证码有效时间（秒）", example = "600")
    private int expireSeconds;

    @Schema(description = "重发冷却时间（秒），0表示可立即发送", example = "60")
    private int cooldownSeconds;

    @Schema(description = "邮箱（脱敏显示）", example = "te***@example.com")
    private String maskedEmail;
}
