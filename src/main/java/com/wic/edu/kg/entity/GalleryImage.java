package com.wic.edu.kg.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图片库实体
 */
@Data
@TableName("gallery_image")
public class GalleryImage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 上传用户ID
     */
    private Long userId;
    
    /**
     * 图片标题
     */
    private String title;
    
    /**
     * 图片描述
     */
    private String description;
    
    /**
     * 拍摄地点
     */
    private String location;
    
    /**
     * 图片URL (R2存储)
     */
    private String imageUrl;
    
    /**
     * 缩略图URL
     */
    private String thumbnailUrl;
    
    /**
     * 图片宽度
     */
    private Integer width;
    
    /**
     * 图片高度
     */
    private Integer height;
    
    /**
     * 文件大小(字节)
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 分类: campus/activity/landscape/life/other
     */
    private String category;
    
    /**
     * 标签(逗号分隔)
     */
    private String tags;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 状态: 0-待审核, 1-已通过, 2-已拒绝
     */
    private Integer status;
    
    /**
     * 拒绝原因
     */
    private String rejectReason;
    
    /**
     * 审核人ID
     */
    private Long reviewedBy;
    
    /**
     * 审核时间
     */
    private LocalDateTime reviewedAt;
    
    /**
     * 是否精选
     */
    private Integer isFeatured;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
