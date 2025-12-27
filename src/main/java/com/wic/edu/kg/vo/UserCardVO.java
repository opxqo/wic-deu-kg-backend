package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "用户公开名片信息")
public class UserCardVO {

    @Schema(description = "学号")
    private String studentId;

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

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "加入时间")
    private LocalDateTime joinedAt;
}
