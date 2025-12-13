package com.wic.edu.kg.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 学号（唯一）
     */
    @TableField("student_id")
    private String studentId;

    /**
     * 密码（加密存储）
     */
    @TableField("password")
    private String password;

    /**
     * 真实姓名
     */
    @TableField("name")
    private String name;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 院系
     */
    @TableField("department")
    private String department;

    /**
     * 专业
     */
    @TableField("major")
    private String major;

    /**
     * 个人简介
     */
    @TableField("bio")
    private String bio;

    /**
     * 状态：0-未激活，1-正常，2-禁用
     */
    @TableField("status")
    private Integer status;

    /**
     * 用户角色：1-组织者，2-管理员，3-普通用户
     */
    @TableField("role")
    private Integer role;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
