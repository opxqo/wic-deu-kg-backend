package com.wic.edu.kg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "注册请求参数")
public class RegisterRequest {

    @NotBlank(message = "学号不能为空")
    @Schema(description = "学号", example = "202103058", required = true)
    private String studentId;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度应在2-50个字符之间")
    @Schema(description = "用户名", example = "alex_zhang", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度应在6-100个字符之间")
    @Schema(description = "密码", example = "password123", required = true)
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "真实姓名", example = "张三", required = true)
    private String name;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "alex.zhang@wic.edu.kg")
    private String email;

    @Schema(description = "院系", example = "信息工程学院")
    private String department;

    @Schema(description = "专业", example = "软件工程")
    private String major;
}
