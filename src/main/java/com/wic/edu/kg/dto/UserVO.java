package com.wic.edu.kg.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息VO")
public class UserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "学号")
    private String studentId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "院系")
    private String department;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "状态: 0-未激活, 1-正常, 2-禁用")
    private Integer status;

    @Schema(description = "角色: 1-组织者, 2-管理员, 3-普通用户")
    private Integer role;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "注册时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
