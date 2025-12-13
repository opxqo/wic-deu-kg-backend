package com.wic.edu.kg.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图片点赞实体
 */
@Data
@TableName("gallery_like")
public class GalleryLike {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 图片ID
     */
    @TableField("photo_id")
    private Long imageId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
