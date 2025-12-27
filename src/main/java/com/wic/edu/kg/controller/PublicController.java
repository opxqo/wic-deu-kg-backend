package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.service.*;
import com.wic.edu.kg.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公开接口控制器
 * 无需登录即可访问的所有接口
 */
@RestController
@RequestMapping("/api/public")
@Tag(name = "公开接口", description = "无需登录即可访问的接口")
public class PublicController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private FoodStoreService foodStoreService;

    @Autowired
    private GalleryService galleryService;

    // ==================== 用户名片 ====================

    @GetMapping("/users/card/{studentId}")
    @Operation(summary = "获取用户名片", description = "根据学号获取用户公开信息（姓名、头像、院系、简介等）")
    public Result<UserCardVO> getUserCard(
            @Parameter(description = "学号", required = true) @PathVariable String studentId) {
        UserCardVO userCard = sysUserService.getUserCard(studentId);
        return Result.success(userCard);
    }

    // ==================== 学部信息 ====================

    @GetMapping("/departments")
    @Operation(summary = "获取所有学部", description = "获取所有启用的学部及其辅导员信息")
    public Result<List<DepartmentVO>> getAllDepartments() {
        List<DepartmentVO> departments = departmentService.getAllDepartments();
        return Result.success(departments);
    }

    @GetMapping("/departments/{id}")
    @Operation(summary = "获取学部详情", description = "根据ID获取学部详细信息")
    public Result<DepartmentVO> getDepartmentById(
            @Parameter(description = "学部ID") @PathVariable Long id) {
        DepartmentVO department = departmentService.getDepartmentById(id);
        if (department == null) {
            return Result.error("学部不存在");
        }
        return Result.success(department);
    }

    // ==================== 美食店铺 ====================

    @GetMapping("/food/stores")
    @Operation(summary = "获取店铺列表", description = "获取美食店铺列表，支持分页和筛选")
    public Result<Page<FoodStoreVO>> getStoreList(
            @Parameter(description = "位置筛选") @RequestParam(required = false) String location,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        com.wic.edu.kg.dto.StoreQueryRequest request = new com.wic.edu.kg.dto.StoreQueryRequest();
        request.setLocation(location);
        request.setKeyword(keyword);
        request.setPage(page);
        request.setSize(size);
        Page<FoodStoreVO> result = foodStoreService.getStoreList(request, null);
        return Result.success(result);
    }

    @GetMapping("/food/stores/{id}")
    @Operation(summary = "获取店铺详情", description = "根据ID获取店铺详细信息")
    public Result<FoodStoreVO> getStoreById(
            @Parameter(description = "店铺ID") @PathVariable Long id) {
        FoodStoreVO store = foodStoreService.getStoreDetail(id, null);
        if (store == null) {
            return Result.error("店铺不存在");
        }
        return Result.success(store);
    }

    // ==================== 图片库 ====================

    @GetMapping("/gallery")
    @Operation(summary = "获取图片列表", description = "获取已审核通过的图片列表，支持分类筛选和关键词搜索")
    public Result<Page<GalleryImageVO>> getGalleryImages(
            @Parameter(description = "分类筛选") @RequestParam(required = false) String category,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        Page<GalleryImageVO> result = galleryService.getApprovedImages(category, keyword, page, size, null);
        return Result.success(result);
    }

    @GetMapping("/gallery/featured")
    @Operation(summary = "获取精选图片", description = "获取精选图片列表，用于首页展示")
    public Result<List<GalleryImageVO>> getFeaturedImages(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "8") int limit) {
        List<GalleryImageVO> images = galleryService.getFeaturedImages(limit);
        return Result.success(images);
    }

    @GetMapping("/gallery/{id}")
    @Operation(summary = "获取图片详情", description = "获取单张图片详情")
    public Result<GalleryImageVO> getGalleryImageDetail(
            @Parameter(description = "图片ID") @PathVariable Long id) {
        GalleryImageVO image = galleryService.getImageDetail(id, null);
        if (image == null) {
            return Result.error("图片不存在");
        }
        return Result.success(image);
    }
}
