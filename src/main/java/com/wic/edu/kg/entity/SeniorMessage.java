package com.wic.edu.kg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学长学姐留言实体
 */
@Data
@TableName("senior_message")
@Schema(description = "学长学姐留言")
public class SeniorMessage {

    @TableId(type = IdType.AUTO)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "发布者ID")
    private Long userId;

    @Schema(description = "留言内容")
    private String content;

    @Schema(description = "署名")
    private String signature;

    @Schema(description = "便签颜色")
    private String cardColor;

    @Schema(description = "墨水颜色")
    private String inkColor;

    @Schema(description = "字体ID")
    private Long fontId;

    @Schema(description = "状态:0-待审核,1-已发布,2-已拒绝")
    private Integer status;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
