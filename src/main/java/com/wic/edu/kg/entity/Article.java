package com.wic.edu.kg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学院动态实体
 */
@Data
@TableName("article")
@Schema(description = "学院动态")
public class Article {

    @TableId(type = IdType.AUTO)
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

    @Schema(description = "标签(逗号分隔)")
    private String tags;

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
