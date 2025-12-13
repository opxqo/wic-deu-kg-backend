package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wic.edu.kg.annotation.RequireRole;
import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.entity.GalleryImage;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.enums.UserRole;
import com.wic.edu.kg.service.GalleryService;
import com.wic.edu.kg.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

/**
 * 图片库管理API（需要管理员权限）
 */
@RestController
@RequestMapping("/api/admin/gallery")
@Tag(name = "光影城院-管理", description = "图片审核和管理接口")
@RequireRole(UserRole.ADMIN)
public class GalleryAdminController {

    @Autowired
    private GalleryService galleryService;
    
    @Autowired
    private SysUserService sysUserService;
    
    private SysUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return sysUserService.getByStudentId(user.getUsername());
        }
        return null;
    }

    @GetMapping
    @Operation(summary = "获取所有图片", description = "获取所有图片，可按状态筛选")
    public Result<Page<GalleryImage>> getAllImages(
            @Parameter(description = "状态筛选: 0-待审核, 1-已通过, 2-已拒绝") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        Page<GalleryImage> result = galleryService.getAllImages(status, page, size);
        return Result.success(result);
    }

    @GetMapping("/pending")
    @Operation(summary = "获取待审核图片", description = "获取所有待审核的图片")
    public Result<Page<GalleryImage>> getPendingImages(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        Page<GalleryImage> result = galleryService.getAllImages(0, page, size);
        return Result.success(result);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "审核通过", description = "通过图片审核")
    public Result<Void> approveImage(
            @Parameter(description = "图片ID") @PathVariable Long id) {
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        galleryService.reviewImage(id, true, null, currentUser.getId());
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "审核拒绝", description = "拒绝图片审核")
    public Result<Void> rejectImage(
            @Parameter(description = "图片ID") @PathVariable Long id,
            @Parameter(description = "拒绝原因") @RequestParam(required = false) String reason) {
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        galleryService.reviewImage(id, false, reason, currentUser.getId());
        return Result.success();
    }

    @PutMapping("/{id}/featured")
    @Operation(summary = "设置/取消精选", description = "设置或取消图片精选状态")
    public Result<Void> setFeatured(
            @Parameter(description = "图片ID") @PathVariable Long id,
            @Parameter(description = "是否精选") @RequestParam boolean featured) {
        galleryService.setFeatured(id, featured);
        return Result.success();
    }

    @PutMapping("/{id}/sort-order")
    @Operation(summary = "设置排序", description = "设置图片排序值")
    public Result<Void> setSortOrder(
            @Parameter(description = "图片ID") @PathVariable Long id,
            @Parameter(description = "排序值") @RequestParam int sortOrder) {
        GalleryImage image = galleryService.getById(id);
        if (image == null) {
            return Result.error("图片不存在");
        }
        image.setSortOrder(sortOrder);
        galleryService.updateById(image);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除图片", description = "删除任意图片")
    public Result<Void> deleteImage(
            @Parameter(description = "图片ID") @PathVariable Long id) {
        galleryService.deleteImage(id);
        return Result.success();
    }

    @PutMapping("/batch/approve")
    @Operation(summary = "批量审核通过", description = "批量通过多张图片")
    public Result<Void> batchApprove(
            @Parameter(description = "图片ID列表") @RequestBody Long[] ids) {
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        for (Long id : ids) {
            galleryService.reviewImage(id, true, null, currentUser.getId());
        }
        return Result.success();
    }

    @PutMapping("/batch/reject")
    @Operation(summary = "批量审核拒绝", description = "批量拒绝多张图片")
    public Result<Void> batchReject(
            @Parameter(description = "图片ID列表") @RequestBody Long[] ids,
            @Parameter(description = "拒绝原因") @RequestParam(required = false) String reason) {
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        for (Long id : ids) {
            galleryService.reviewImage(id, false, reason, currentUser.getId());
        }
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除", description = "批量删除多张图片")
    public Result<Void> batchDelete(
            @Parameter(description = "图片ID列表") @RequestBody Long[] ids) {
        for (Long id : ids) {
            galleryService.deleteImage(id);
        }
        return Result.success();
    }
}
