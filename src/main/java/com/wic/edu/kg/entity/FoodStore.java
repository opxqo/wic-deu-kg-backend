package com.wic.edu.kg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("food_store")
public class FoodStore {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String location;

    private String description;

    private String image;

    private BigDecimal rating;

    private Integer reviewCount;

    private Integer likes;

    /**
     * 人均价格
     */
    private BigDecimal avgPrice;

    private String businessHours;

    private String contact;

    private String tags;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
