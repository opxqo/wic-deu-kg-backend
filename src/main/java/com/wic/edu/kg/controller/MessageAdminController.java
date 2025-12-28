package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wic.edu.kg.annotation.RequireRole;
import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.enums.UserRole;
import com.wic.edu.kg.service.SeniorMessageService;
import com.wic.edu.kg.vo.SeniorMessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 留言管理API（需要管理员权限）
 */
@RestController
@RequestMapping("/api/admin/messages")
@Tag(name = "留言管理", description = "学长学姐留言审核和管理接口，需要管理员权限")
@RequireRole(UserRole.ADMIN)
public class MessageAdminController {

    @Autowired
    private SeniorMessageService seniorMessageService;

    // ==================== 查询接口 ====================

    @GetMapping
    @Operation(summary = "获取所有留言", description = "获取所有留言，可按状态筛选")
    @ApiResponse(responseCode = "200", description = "获取成功")
    public Result<Page<SeniorMessageVO>> getAllMessages(
            @Parameter(description = "状态筛选: 0-待审核, 1-已发布, 2-已拒绝") @RequestParam(required = false) Integer status,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "20") @RequestParam(defaultValue = "20") int size) {
        Page<SeniorMessageVO> result = seniorMessageService.adminGetMessages(status, keyword, page, size);
        return Result.success(result);
    }

    @GetMapping("/pending")
    @Operation(summary = "获取待审核留言", description = "获取所有待审核的留言")
    @ApiResponse(responseCode = "200", description = "获取成功")
    public Result<Page<SeniorMessageVO>> getPendingMessages(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "20") @RequestParam(defaultValue = "20") int size) {
        Page<SeniorMessageVO> result = seniorMessageService.adminGetMessages(0, null, page, size);
        return Result.success(result);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取统计信息", description = "获取留言各状态数量统计")
    @ApiResponse(responseCode = "200", description = "获取成功")
    public Result<MessageStats> getStats() {
        long pending = seniorMessageService.countByStatus(0);
        long published = seniorMessageService.countByStatus(1);
        long rejected = seniorMessageService.countByStatus(2);
        return Result.success(new MessageStats(pending, published, rejected, pending + published + rejected));
    }

    // ==================== 审核接口 ====================

    @PutMapping("/{id}/approve")
    @Operation(summary = "审核通过", description = "通过留言审核")
    @ApiResponse(responseCode = "200", description = "操作成功")
    public Result<Void> approveMessage(
            @Parameter(description = "留言ID", required = true) @PathVariable Long id) {
        seniorMessageService.reviewMessage(id, 1, null);
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "审核拒绝", description = "拒绝留言审核")
    @ApiResponse(responseCode = "200", description = "操作成功")
    public Result<Void> rejectMessage(
            @Parameter(description = "留言ID", required = true) @PathVariable Long id,
            @Parameter(description = "拒绝原因") @RequestParam(required = false) String reason) {
        seniorMessageService.reviewMessage(id, 2, reason);
        return Result.success();
    }

    // ==================== 留言管理 ====================

    @PutMapping("/{id}/status")
    @Operation(summary = "修改留言状态", description = "直接修改留言状态")
    @ApiResponse(responseCode = "200", description = "操作成功")
    public Result<Void> updateStatus(
            @Parameter(description = "留言ID", required = true) @PathVariable Long id,
            @Parameter(description = "新状态: 0-待审核, 1-已发布, 2-已拒绝", required = true) @RequestParam Integer status) {
        seniorMessageService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除留言", description = "删除任意留言")
    @ApiResponse(responseCode = "200", description = "操作成功")
    public Result<Void> deleteMessage(
            @Parameter(description = "留言ID", required = true) @PathVariable Long id) {
        seniorMessageService.adminDeleteMessage(id);
        return Result.success();
    }

    // ==================== 批量操作 ====================

    @PutMapping("/batch/approve")
    @Operation(summary = "批量审核通过", description = "批量通过多条留言")
    @ApiResponse(responseCode = "200", description = "操作成功")
    public Result<Integer> batchApprove(
            @Parameter(description = "留言ID列表", required = true) @RequestBody List<Long> ids) {
        int count = seniorMessageService.batchReview(ids, 1, null);
        return Result.success(count);
    }

    @PutMapping("/batch/reject")
    @Operation(summary = "批量审核拒绝", description = "批量拒绝多条留言")
    @ApiResponse(responseCode = "200", description = "操作成功")
    public Result<Integer> batchReject(
            @Parameter(description = "留言ID列表", required = true) @RequestBody List<Long> ids,
            @Parameter(description = "拒绝原因") @RequestParam(required = false) String reason) {
        int count = seniorMessageService.batchReview(ids, 2, reason);
        return Result.success(count);
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除", description = "批量删除多条留言")
    @ApiResponse(responseCode = "200", description = "操作成功")
    public Result<Integer> batchDelete(
            @Parameter(description = "留言ID列表", required = true) @RequestBody List<Long> ids) {
        int count = seniorMessageService.batchDelete(ids);
        return Result.success(count);
    }

    /**
     * 留言统计信息
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class MessageStats {
        @Parameter(description = "待审核数量")
        private long pending;
        @Parameter(description = "已发布数量")
        private long published;
        @Parameter(description = "已拒绝数量")
        private long rejected;
        @Parameter(description = "总数量")
        private long total;
    }
}
