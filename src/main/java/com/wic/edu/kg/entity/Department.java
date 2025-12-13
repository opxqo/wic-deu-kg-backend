package com.wic.edu.kg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学部实体
 */
@Data
@TableName("department")
@Schema(description = "学部")
public class Department {
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "学部代码")
    private String code;
    
    @Schema(description = "中文名称")
    private String nameZh;
    
    @Schema(description = "英文名称")
    private String nameEn;
    
    @Schema(description = "图标名称")
    private String icon;
    
    @Schema(description = "中文描述")
    private String descriptionZh;
    
    @Schema(description = "英文描述")
    private String descriptionEn;
    
    @Schema(description = "位置")
    private String location;
    
    @Schema(description = "热门专业(中文)")
    private String hotMajorZh;
    
    @Schema(description = "热门专业(英文)")
    private String hotMajorEn;
    
    @Schema(description = "在线人数")
    private Integer onlineCount;
    
    @Schema(description = "排序顺序")
    private Integer sortOrder;
    
    @Schema(description = "状态: 1-启用, 0-禁用")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
