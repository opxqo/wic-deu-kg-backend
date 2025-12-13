package com.wic.edu.kg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户信息请求（用户自己修改）
 */
@Data
@Schema(description = "更新用户信息请求")
public class UpdateProfileRequest {
    
    @Schema(description = "用户名", example = "newusername")
    @Size(min = 2, max = 20, message = "用户名长度需要在2-20个字符之间")
    private String username;
    
    @Schema(description = "真实姓名", example = "张三")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;
    
    @Schema(description = "邮箱", example = "newemail@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    @Schema(description = "院系", example = "计算机学院")
    @Size(max = 50, message = "院系名称不能超过50个字符")
    private String department;
    
    @Schema(description = "专业", example = "软件工程")
    @Size(max = 50, message = "专业名称不能超过50个字符")
    private String major;
    
    @Schema(description = "个人简介", example = "热爱编程的学生")
    @Size(max = 500, message = "个人简介不能超过500个字符")
    private String bio;
}
