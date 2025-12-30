package com.wic.edu.kg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 创建/更新学院动态请求
 */
@Data
@Schema(description = "创建/更新学院动态请求")
public class ArticleRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题不能超过200字符")
    @Schema(description = "标题")
    private String title;

    @Size(max = 500, message = "副标题不能超过500字符")
    @Schema(description = "副标题")
    private String subtitle;

    @Size(max = 100, message = "作者不能超过100字符")
    @Schema(description = "作者")
    private String author;

    @Schema(description = "发布日期")
    private LocalDate publishDate;

    @Size(max = 50, message = "阅读时长不能超过50字符")
    @Schema(description = "阅读时长")
    private String readTime;

    @Size(max = 500, message = "封面图片URL不能超过500字符")
    @Schema(description = "封面图片URL")
    private String coverImage;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "HTML内容")
    private String content;

    @Schema(description = "状态: 0-草稿, 1-已发布, 2-已下架")
    private Integer status;
}
