package com.wic.edu.kg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 管理员更新用户信息请求
 */
@Data
@Schema(description = "管理员更新用户信息请求")
public class AdminUpdateUserRequest {
    
    @Schema(description = "用户ID", example = "1", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
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
    
    @Schema(description = "状态：0-未激活，1-正常，2-禁用", example = "1")
    @Min(value = 0, message = "状态值无效")
    @Max(value = 2, message = "状态值无效")
    private Integer status;
    
    @Schema(description = "角色：1-组织者，2-管理员，3-普通用户", example = "3")
    @Min(value = 1, message = "角色值无效")
    @Max(value = 3, message = "角色值无效")
    private Integer role;
}
