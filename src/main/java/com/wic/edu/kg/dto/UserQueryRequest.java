package com.wic.edu.kg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 用户列表查询请求
 */
@Data
@Schema(description = "用户列表查询请求")
public class UserQueryRequest {
    
    @Schema(description = "当前页码", example = "1")
    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "10")
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 10;
    
    @Schema(description = "搜索关键词（学号/用户名/姓名/邮箱）", example = "张三")
    private String keyword;
    
    @Schema(description = "状态筛选：0-未激活，1-正常，2-禁用", example = "1")
    @Min(value = 0, message = "状态值无效")
    @Max(value = 2, message = "状态值无效")
    private Integer status;
    
    @Schema(description = "角色筛选：1-组织者，2-管理员，3-普通用户", example = "3")
    @Min(value = 1, message = "角色值无效")
    @Max(value = 3, message = "角色值无效")
    private Integer role;
    
    @Schema(description = "院系筛选", example = "计算机学院")
    private String department;
    
    @Schema(description = "排序字段", example = "createdAt")
    private String orderBy = "createdAt";
    
    @Schema(description = "排序方向：asc/desc", example = "desc")
    private String orderDirection = "desc";
}
