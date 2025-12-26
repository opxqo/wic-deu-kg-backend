package com.wic.edu.kg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "重置密码请求参数")
public class ResetPasswordRequest {

    @NotBlank(message = "学号不能为空")
    @Schema(description = "学号", example = "202103058", required = true)
    private String studentId;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "注册时使用的邮箱", example = "student@wic.edu.kg", required = true)
    private String email;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度应在6-100个字符之间")
    @Schema(description = "新密码", example = "newpassword123", required = true)
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认新密码", example = "newpassword123", required = true)
    private String confirmPassword;
}
