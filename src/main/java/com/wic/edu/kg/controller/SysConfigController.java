package com.wic.edu.kg.controller;

import com.wic.edu.kg.annotation.RequireRole;
import com.wic.edu.kg.common.ApiResponse;
import com.wic.edu.kg.enums.UserRole;
import com.wic.edu.kg.service.SysConfigService;
import com.wic.edu.kg.vo.SysConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 系统配置控制器（仅组织者）
 *
 * RESTful 设计 - 系统配置接口：
 * - GET /api/admin/config - 获取系统配置
 * - PUT /api/admin/config - 更新系统配置
 */
@RestController
@RequestMapping("/api/admin/config")
@Tag(name = "系统配置", description = "系统配置管理接口（仅组织者可访问）")
// @RequireRole(UserRole.ORGANIZER) // TODO: 测试完成后恢复权限限制
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @GetMapping
    @Operation(summary = "获取系统配置", description = "获取当前系统配置状态")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ResponseEntity<ApiResponse<SysConfigVO>> getConfig() {
        SysConfigVO config = SysConfigVO.builder()
                .maintenanceMode(sysConfigService.isMaintenanceMode())
                .openRegistration(sysConfigService.isOpenRegistration())
                .build();
        return ResponseEntity.ok(ApiResponse.ok(config));
    }

    @PutMapping
    @Operation(summary = "更新系统配置", description = "更新系统配置")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ResponseEntity<ApiResponse<SysConfigVO>> updateConfig(@RequestBody SysConfigVO config) {
        if (config.getMaintenanceMode() != null) {
            sysConfigService.setMaintenanceMode(config.getMaintenanceMode());
        }
        if (config.getOpenRegistration() != null) {
            sysConfigService.setOpenRegistration(config.getOpenRegistration());
        }

        // 返回更新后的配置
        SysConfigVO updatedConfig = SysConfigVO.builder()
                .maintenanceMode(sysConfigService.isMaintenanceMode())
                .openRegistration(sysConfigService.isOpenRegistration())
                .build();
        return ResponseEntity.ok(ApiResponse.ok(updatedConfig));
    }
}
