package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统配置VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统配置")
public class SysConfigVO {

    @Schema(description = "维护模式", example = "false")
    private Boolean maintenanceMode;

    @Schema(description = "开放注册", example = "true")
    private Boolean openRegistration;
}
