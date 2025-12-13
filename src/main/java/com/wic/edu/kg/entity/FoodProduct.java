package com.wic.edu.kg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 美食商品实体
 */
@Data
@TableName("food_product")
public class FoodProduct {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属店铺ID
     */
    private Long storeId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 评分 1-5
     */
    private BigDecimal rating;

    /**
     * 评论数
     */
    private Integer reviewCount;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 分类 (如: 🔥 Chef Choice, ☕ Coffee, 🍰 Dessert)
     */
    private String category;

    /**
     * 标签 JSON格式 ["Spicy", "Popular"]
     */
    private String tags;

    /**
     * 辣度 0-3
     */
    private Integer spiciness;

    /**
     * 状态 0-下架 1-上架 (映射数据库 available 字段)
     */
    @TableField("available")
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
