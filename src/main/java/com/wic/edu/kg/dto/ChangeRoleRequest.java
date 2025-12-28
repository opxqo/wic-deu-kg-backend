package com.wic.edu.kg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改用户角色请求
 */
@Data
@Schema(description = "修改用户角色请求")
public class ChangeRoleRequest {

    @Schema(description = "用户ID", example = "1", hidden = true)
    private Long userId;

    @Schema(description = "新角色：1-组织者，2-管理员，3-普通用户", example = "2", required = true)
    @NotNull(message = "角色不能为空")
    @Min(value = 1, message = "角色值无效")
    @Max(value = 3, message = "角色值无效")
    private Integer role;
}
