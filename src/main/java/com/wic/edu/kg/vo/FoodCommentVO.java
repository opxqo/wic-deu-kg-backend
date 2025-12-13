package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "评论VO")
public class FoodCommentVO {

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String user;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "评分")
    private BigDecimal rating;

    @Schema(description = "点赞数")
    private Integer likes;

    @Schema(description = "发布时间")
    private LocalDateTime createdAt;

    @Schema(description = "相对时间（如：2天前）")
    private String relativeTime;
}
