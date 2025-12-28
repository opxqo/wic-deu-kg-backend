package com.wic.edu.kg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 账号激活请求
 */
@Data
@Schema(description = "账号激活请求参数")
public class ActivateAccountRequest {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "注册时使用的邮箱地址", example = "student@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码必须为6位")
    @Schema(description = "邮箱收到的6位数字验证码", example = "123456", minLength = 6, maxLength = 6, requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;
}
