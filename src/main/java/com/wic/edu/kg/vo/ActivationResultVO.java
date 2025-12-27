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
@Schema(description = "账号激活响应")
public class ActivationResultVO {

    @Schema(description = "是否激活成功")
    private boolean success;

    @Schema(description = "提示消息")
    private String message;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "学号")
    private String studentId;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "激活时间")
    private LocalDateTime activatedAt;
}
