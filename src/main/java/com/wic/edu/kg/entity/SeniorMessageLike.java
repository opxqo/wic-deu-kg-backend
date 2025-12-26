package com.wic.edu.kg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 留言点赞实体
 */
@Data
@TableName("senior_message_like")
public class SeniorMessageLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long messageId;

    private Long userId;

    private LocalDateTime createdAt;
}
