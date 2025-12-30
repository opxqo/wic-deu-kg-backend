package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学院动态响应视图对象
 */
@Data
@Schema(description = "学院动态详情")
public class ArticleVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "副标题")
    private String subtitle;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "发布日期")
    private LocalDate publishDate;

    @Schema(description = "阅读时长")
    private String readTime;

    @Schema(description = "封面图片URL")
    private String coverImage;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "HTML内容")
    private String content;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "状态: 0-草稿, 1-已发布, 2-已下架")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
