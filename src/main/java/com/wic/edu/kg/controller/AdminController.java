package com.wic.edu.kg.controller;

import com.wic.edu.kg.annotation.RequireRole;
import com.wic.edu.kg.common.ApiResponse;
import com.wic.edu.kg.dto.*;
import com.wic.edu.kg.enums.UserRole;
import com.wic.edu.kg.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器（管理员）
 * 
 * RESTful 设计 - 管理员对用户资源的操作：
 * - GET /api/users - 查询用户列表（需管理员权限）
 * - GET /api/users/{id} - 获取用户详情（需管理员权限）
 * - PATCH /api/users/{id} - 更新用户信息（需管理员权限）
 * - DELETE /api/users/{id} - 删除用户（需管理员权限）
 * - PATCH /api/users/{id}/role - 修改用户角色（需组织者权限）
 * - PATCH /api/users/{id}/status - 修改用户状态（需管理员权限）
 * - PATCH /api/users/{id}/password - 重置用户密码（需管理员权限）
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理（管理员）", description = "管理员用户管理功能")
@RequireRole(UserRole.ADMIN)
public class AdminController {

    @Autowired
    private SysUserService sysUserService;

    private String getCurrentStudentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // ==================== 用户查询接口 ====================

    @GetMapping
    @Operation(summary = "查询用户列表", description = "分页查询用户，支持关键词搜索、状态/角色/院系筛选、排序")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ResponseEntity<ApiResponse<PageResponse<UserVO>>> queryUsers(@Validated UserQueryRequest request) {
        PageResponse<UserVO> result = sysUserService.queryUsers(request);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<ApiResponse<UserVO>> getUserById(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        UserVO user = sysUserService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }

    // ==================== 用户管理接口 ====================

    @PatchMapping("/{userId}")
    @Operation(summary = "更新用户信息", description = "管理员更新用户信息（包括基本信息、状态等）")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<ApiResponse<UserVO>> updateUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Validated @RequestBody AdminUpdateUserRequest request) {
        request.setUserId(userId);
        String currentStudentId = getCurrentStudentId();
        UserVO user = sysUserService.adminUpdateUser(currentStudentId, request);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }

    @PatchMapping("/{userId}/role")
    @RequireRole(UserRole.ORGANIZER)
    @Operation(summary = "修改用户角色", description = "只有组织者可以修改用户角色")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "修改成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "不能修改自己的角色"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "需要组织者权限")
    })
    public ResponseEntity<ApiResponse<String>> changeUserRole(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Validated @RequestBody ChangeRoleRequest request) {
        request.setUserId(userId);
        String currentStudentId = getCurrentStudentId();
        sysUserService.changeUserRole(currentStudentId, request);
        return ResponseEntity.ok(ApiResponse.ok("用户角色修改成功"));
    }

    @PatchMapping("/{userId}/status")
    @Operation(summary = "修改用户状态", description = "启用/禁用用户账号")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "修改成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "不能修改自己的状态")
    })
    public ResponseEntity<ApiResponse<String>> changeUserStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Validated @RequestBody ChangeStatusRequest request) {
        request.setUserId(userId);
        String currentStudentId = getCurrentStudentId();
        sysUserService.changeUserStatus(currentStudentId, request);
        return ResponseEntity.ok(ApiResponse.ok("用户状态修改成功"));
    }

    @PatchMapping("/{userId}/password")
    @Operation(summary = "重置用户密码", description = "管理员重置用户密码")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "重置成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<ApiResponse<String>> resetUserPassword(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "新密码", required = true) @RequestParam String newPassword) {
        String currentStudentId = getCurrentStudentId();
        sysUserService.adminResetPassword(currentStudentId, userId, newPassword);
        return ResponseEntity.ok(ApiResponse.ok("密码重置成功"));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "逻辑删除用户账号")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "不能删除自己")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        String currentStudentId = getCurrentStudentId();
        sysUserService.deleteUser(currentStudentId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除用户", description = "批量逻辑删除用户账号")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "不能删除自己")
    })
    public ResponseEntity<Void> batchDeleteUsers(@RequestBody List<Long> userIds) {
        String currentStudentId = getCurrentStudentId();
        sysUserService.batchDeleteUsers(currentStudentId, userIds);
        return ResponseEntity.noContent().build();
    }
}
