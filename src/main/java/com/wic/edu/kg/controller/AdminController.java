package com.wic.edu.kg.controller;

import com.wic.edu.kg.annotation.RequireRole;
import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.dto.*;
import com.wic.edu.kg.enums.UserRole;
import com.wic.edu.kg.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员接口
 * 需要管理员（ADMIN）或组织者（ORGANIZER）权限
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员接口", description = "用户管理、权限管理等管理功能")
@RequireRole(UserRole.ADMIN)  // 整个控制器默认需要管理员权限
public class AdminController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 获取当前操作者的学号
     */
    private String getCurrentStudentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // ==================== 用户查询接口 ====================

    @GetMapping("/users")
    @Operation(summary = "分页查询用户列表", description = "支持关键词搜索、状态/角色/院系筛选、排序")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    public Result<PageResponse<UserVO>> queryUsers(@Validated UserQueryRequest request) {
        PageResponse<UserVO> result = sysUserService.queryUsers(request);
        return Result.success(result);
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public Result<UserVO> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        UserVO user = sysUserService.getUserById(userId);
        return Result.success(user);
    }

    // ==================== 用户管理接口 ====================

    @PutMapping("/users")
    @Operation(summary = "更新用户信息", description = "管理员更新用户信息（包括状态、角色等）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public Result<UserVO> updateUser(@Validated @RequestBody AdminUpdateUserRequest request) {
        String currentStudentId = getCurrentStudentId();
        UserVO user = sysUserService.adminUpdateUser(currentStudentId, request);
        return Result.success("用户信息更新成功", user);
    }

    @PutMapping("/users/role")
    @RequireRole(UserRole.ORGANIZER)  // 修改角色需要组织者权限
    @Operation(summary = "修改用户角色", description = "只有组织者可以修改用户角色")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "400", description = "参数错误/不能修改自己的角色"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "权限不足（需要组织者权限）"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public Result<String> changeUserRole(@Validated @RequestBody ChangeRoleRequest request) {
        String currentStudentId = getCurrentStudentId();
        sysUserService.changeUserRole(currentStudentId, request);
        return Result.success("用户角色修改成功");
    }

    @PutMapping("/users/status")
    @Operation(summary = "修改用户状态", description = "启用/禁用用户账号")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "400", description = "参数错误/不能修改自己的状态"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public Result<String> changeUserStatus(@Validated @RequestBody ChangeStatusRequest request) {
        String currentStudentId = getCurrentStudentId();
        sysUserService.changeUserStatus(currentStudentId, request);
        return Result.success("用户状态修改成功");
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "删除用户", description = "逻辑删除用户账号")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "不能删除自己"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public Result<String> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        String currentStudentId = getCurrentStudentId();
        sysUserService.deleteUser(currentStudentId, userId);
        return Result.success("用户删除成功");
    }

    @DeleteMapping("/users/batch")
    @Operation(summary = "批量删除用户", description = "批量逻辑删除用户账号")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "参数错误/不能删除自己"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    public Result<String> batchDeleteUsers(@RequestBody List<Long> userIds) {
        String currentStudentId = getCurrentStudentId();
        sysUserService.batchDeleteUsers(currentStudentId, userIds);
        return Result.success("批量删除成功");
    }

    @PostMapping("/users/{userId}/reset-password")
    @Operation(summary = "重置用户密码", description = "管理员重置用户密码")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "重置成功"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public Result<String> resetUserPassword(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        String currentStudentId = getCurrentStudentId();
        sysUserService.adminResetPassword(currentStudentId, userId, newPassword);
        return Result.success("密码重置成功");
    }
}
