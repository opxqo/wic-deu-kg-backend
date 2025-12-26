package com.wic.edu.kg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建留言请求
 */
@Data
@Schema(description = "创建留言请求")
public class CreateMessageRequest {

    @NotBlank(message = "留言内容不能为空")
    @Size(max = 500, message = "留言内容不能超过500字")
    @Schema(description = "留言内容", required = true, example = "欢迎来到毕业生留言板！")
    private String content;

    @Size(max = 50, message = "署名不能超过50字")
    @Schema(description = "署名", example = "24届 学姐")
    private String signature;

    @Schema(description = "便签颜色", example = "#FFF8DC", defaultValue = "#FFF8DC")
    private String cardColor = "#FFF8DC";

    @Schema(description = "墨水颜色", example = "#1a365d", defaultValue = "#1a365d")
    private String inkColor = "#1a365d";

    @Schema(description = "字体ID", example = "1", defaultValue = "1")
    private Long fontId = 1L;
}
