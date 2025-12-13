package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "店铺VO")
public class FoodStoreVO {

    @Schema(description = "店铺ID")
    private Long id;

    @Schema(description = "店铺名称")
    private String name;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "图片URL")
    private String image;

    @Schema(description = "评分")
    private BigDecimal rating;

    @Schema(description = "评论数")
    private Integer reviewCount;

    @Schema(description = "点赞数")
    private Integer likes;

    @Schema(description = "人均价格")
    private BigDecimal avgPrice;

    @Schema(description = "营业时间")
    private String businessHours;

    @Schema(description = "联系方式")
    private String contact;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "当前用户是否已点赞")
    private Boolean isLiked;

    @Schema(description = "商品列表（详情页使用）")
    private List<FoodProductVO> products;
}
