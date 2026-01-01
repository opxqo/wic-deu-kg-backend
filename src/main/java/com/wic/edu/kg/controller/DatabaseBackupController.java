package com.wic.edu.kg.controller;

import com.wic.edu.kg.annotation.RequireRole;
import com.wic.edu.kg.common.ApiResponse;
import com.wic.edu.kg.enums.UserRole;
import com.wic.edu.kg.service.DatabaseBackupService;
import com.wic.edu.kg.vo.DatabaseBackupVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据库备份控制器（仅组织者）
 *
 * RESTful 设计 - 数据库备份接口：
 * - GET /api/admin/database/backup - 获取数据库备份信息（需组织者权限）
 * - POST /api/admin/database/backup - 执行备份并上传到R2云存储（需组织者权限）
 */
@RestController
@RequestMapping("/api/admin/database")
@Tag(name = "数据库备份管理", description = "数据库备份信息接口（仅组织者可访问）")
// @RequireRole(UserRole.ORGANIZER) // TODO: 测试完成后恢复权限限制
@RequiredArgsConstructor
public class DatabaseBackupController {

    private final DatabaseBackupService databaseBackupService;

    @GetMapping("/backup")
    @Operation(summary = "获取数据库备份信息", description = "获取数据库的详细信息，包括表结构、数据量、大小等（不执行备份）")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足，仅组织者可访问")
    })
    public ResponseEntity<ApiResponse<DatabaseBackupVO>> getDatabaseBackupInfo() {
        DatabaseBackupVO backupInfo = databaseBackupService.getDatabaseBackupInfo();
        return ResponseEntity.ok(ApiResponse.ok(backupInfo));
    }

    @PostMapping("/backup")
    @Operation(summary = "执行数据库备份", description = "执行数据库备份并上传到R2云存储的/db文件夹，返回备份文件URL")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "备份成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足，仅组织者可访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "备份失败")
    })
    public ResponseEntity<ApiResponse<DatabaseBackupVO>> backupAndUpload() {
        DatabaseBackupVO backupInfo = databaseBackupService.backupAndUploadToR2();
        return ResponseEntity.ok(ApiResponse.ok(backupInfo));
    }
}
