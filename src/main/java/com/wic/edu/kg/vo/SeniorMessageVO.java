package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学长学姐留言视图对象
 */
@Data
@Schema(description = "留言视图对象")
public class SeniorMessageVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "留言内容")
    private String content;

    @Schema(description = "署名")
    private String signature;

    @Schema(description = "便签颜色")
    private String cardColor;

    @Schema(description = "墨水颜色")
    private String inkColor;

    @Schema(description = "字体信息")
    private FontInfo font;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "当前用户是否已点赞")
    private Boolean liked;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Data
    @Schema(description = "字体信息")
    public static class FontInfo {
        @Schema(description = "字体ID")
        private Long id;

        @Schema(description = "字体名称")
        private String name;

        @Schema(description = "CSS类名")
        private String cssClass;

        @Schema(description = "font-family值")
        private String fontFamily;
    }
}
