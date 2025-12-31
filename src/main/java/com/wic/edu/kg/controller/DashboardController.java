package com.wic.edu.kg.controller;

import com.wic.edu.kg.annotation.RequireRole;
import com.wic.edu.kg.common.ApiResponse;
import com.wic.edu.kg.enums.UserRole;
import com.wic.edu.kg.service.DashboardService;
import com.wic.edu.kg.vo.DashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘控制器（管理员）
 *
 * RESTful 设计 - 管理面板仪表盘接口：
 * - GET /api/admin/dashboard - 获取仪表盘数据（需管理员权限）
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@Tag(name = "仪表盘管理", description = "管理面板仪表盘数据接口")
@RequireRole(UserRole.ADMIN)
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "获取仪表盘数据", description = "获取管理面板仪表盘的统计数据、待处理项、趋势图表和系统状态")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ResponseEntity<ApiResponse<DashboardVO>> getDashboardData() {
        DashboardVO dashboard = dashboardService.getDashboardData();
        return ResponseEntity.ok(ApiResponse.ok(dashboard));
    }
}
