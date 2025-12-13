package com.wic.edu.kg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "登录请求参数")
public class LoginRequest {

    @NotBlank(message = "学号不能为空")
    @Schema(description = "学号", example = "202103058", required = true)
    private String studentId;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "123456", required = true)
    private String password;
}
