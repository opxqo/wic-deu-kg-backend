package com.wic.edu.kg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字体映射实体
 */
@Data
@TableName("message_font")
@Schema(description = "留言字体")
public class MessageFont {

    @TableId(type = IdType.AUTO)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "字体名称")
    private String name;

    @Schema(description = "CSS类名")
    private String cssClass;

    @Schema(description = "font-family值")
    private String fontFamily;

    @Schema(description = "状态:0-禁用,1-启用")
    private Integer status;

    @Schema(description = "排序")
    private Integer sortOrder;
}
