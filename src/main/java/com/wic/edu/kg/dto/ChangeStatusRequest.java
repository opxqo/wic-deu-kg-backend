package com.wic.edu.kg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改用户状态请求
 */
@Data
@Schema(description = "修改用户状态请求")
public class ChangeStatusRequest {
    
    @Schema(description = "用户ID", example = "1", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @Schema(description = "新状态：0-未激活，1-正常，2-禁用", example = "1", required = true)
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值无效")
    @Max(value = 2, message = "状态值无效")
    private Integer status;
}
