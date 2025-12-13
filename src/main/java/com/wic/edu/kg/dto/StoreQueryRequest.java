package com.wic.edu.kg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "店铺查询请求")
public class StoreQueryRequest {

    @Schema(description = "位置筛选", example = "Big Canteen 1F")
    private String location;

    @Schema(description = "关键词搜索")
    private String keyword;

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer size = 10;

    @Schema(description = "排序字段: rating/likes/reviewCount", example = "rating")
    private String sortBy = "rating";

    @Schema(description = "排序方向: asc/desc", example = "desc")
    private String sortOrder = "desc";
}
