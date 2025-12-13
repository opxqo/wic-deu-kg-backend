package com.wic.edu.kg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Schema(description = "评论请求")
public class CommentRequest {

    @NotBlank(message = "评论内容不能为空")
    @Schema(description = "评论内容", required = true)
    private String content;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1")
    @Max(value = 5, message = "评分最高为5")
    @Schema(description = "评分 1-5", required = true)
    private BigDecimal rating;
}
