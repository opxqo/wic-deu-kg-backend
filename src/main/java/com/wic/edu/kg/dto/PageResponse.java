package com.wic.edu.kg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 分页响应
 */
@Data
@Builder
@Schema(description = "分页响应")
public class PageResponse<T> {
    
    @Schema(description = "数据列表")
    private List<T> records;
    
    @Schema(description = "总记录数")
    private Long total;
    
    @Schema(description = "当前页码")
    private Long current;
    
    @Schema(description = "每页大小")
    private Long size;
    
    @Schema(description = "总页数")
    private Long pages;
}
