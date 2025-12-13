package com.wic.edu.kg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学部辅导员实体
 */
@Data
@TableName("department_counselor")
@Schema(description = "学部辅导员")
public class DepartmentCounselor {
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "所属学部ID")
    private Long departmentId;
    
    @Schema(description = "辅导员姓名")
    private String name;
    
    @Schema(description = "头像URL")
    private String avatar;
    
    @Schema(description = "职称")
    private String title;
    
    @Schema(description = "联系电话")
    private String phone;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "排序顺序")
    private Integer sortOrder;
    
    @Schema(description = "状态: 1-启用, 0-禁用")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
