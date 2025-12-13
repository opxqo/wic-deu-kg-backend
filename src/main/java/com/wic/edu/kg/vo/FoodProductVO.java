package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "商品VO")
public class FoodProductVO {

    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "所属店铺ID")
    private Long storeId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "图片URL")
    private String image;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "评分")
    private BigDecimal rating;

    @Schema(description = "评论数")
    private Integer reviewCount;

    @Schema(description = "点赞数")
    private Integer likes;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "辣度 0-3")
    private Integer spiciness;

    @Schema(description = "当前用户是否已点赞")
    private Boolean isLiked;

    @Schema(description = "精选评论")
    private TopReviewVO topReview;

    @Data
    @Schema(description = "精选评论")
    public static class TopReviewVO {
        @Schema(description = "用户名")
        private String user;
        
        @Schema(description = "头像")
        private String avatar;
        
        @Schema(description = "内容")
        private String content;
    }
}
