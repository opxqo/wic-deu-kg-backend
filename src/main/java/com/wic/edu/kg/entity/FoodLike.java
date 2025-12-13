package com.wic.edu.kg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 美食点赞记录实体
 */
@Data
@TableName("food_like")
public class FoodLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 目标类型: store/product
     */
    private String targetType;

    /**
     * 目标ID (店铺ID或商品ID)
     */
    private Long targetId;

    private LocalDateTime createdAt;
}
