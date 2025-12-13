package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.dto.GalleryUploadDTO;
import com.wic.edu.kg.entity.GalleryImage;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.service.GalleryService;
import com.wic.edu.kg.vo.GalleryImageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 图片库公开API
 */
@RestController
@RequestMapping("/api/gallery")
@Tag(name = "光影城院", description = "图片库公开接口")
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    @GetMapping
    @Operation(summary = "获取图片列表", description = "获取已审核通过的图片列表，支持分类筛选")
    public Result<Page<GalleryImageVO>> getImages(
            @Parameter(description = "分类筛选") @RequestParam(required = false) String category,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal SysUser currentUser) {
        Long userId = currentUser != null ? currentUser.getId() : null;
        Page<GalleryImageVO> result = galleryService.getApprovedImages(category, page, size, userId);
        return Result.success(result);
    }

    @GetMapping("/featured")
    @Operation(summary = "获取精选图片", description = "获取精选图片列表，用于首页展示")
    public Result<List<GalleryImageVO>> getFeaturedImages(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "8") int limit) {
        List<GalleryImageVO> images = galleryService.getFeaturedImages(limit);
        return Result.success(images);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取图片详情", description = "获取单张图片详情")
    public Result<GalleryImageVO> getImageDetail(
            @Parameter(description = "图片ID") @PathVariable Long id,
            @AuthenticationPrincipal SysUser currentUser) {
        Long userId = currentUser != null ? currentUser.getId() : null;
        GalleryImageVO image = galleryService.getImageDetail(id, userId);
        if (image == null) {
            return Result.error("图片不存在");
        }
        // 增加浏览次数
        galleryService.incrementViewCount(id);
        return Result.success(image);
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞/取消点赞", description = "需要登录")
    public Result<Integer> toggleLike(
            @Parameter(description = "图片ID") @PathVariable Long id,
            @AuthenticationPrincipal SysUser currentUser) {
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        int likeCount = galleryService.toggleLike(currentUser.getId(), id);
        return Result.success(likeCount);
    }

    @GetMapping("/categories")
    @Operation(summary = "获取分类列表")
    public Result<List<CategoryInfo>> getCategories() {
        List<CategoryInfo> categories = List.of(
            new CategoryInfo("all", "全部", "All"),
            new CategoryInfo("campus", "校园风光", "Campus"),
            new CategoryInfo("activity", "活动精彩", "Activities"),
            new CategoryInfo("landscape", "自然风景", "Landscape"),
            new CategoryInfo("life", "校园生活", "Campus Life"),
            new CategoryInfo("other", "其他", "Other")
        );
        return Result.success(categories);
    }

    /**
     * 分类信息
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class CategoryInfo {
        private String code;
        private String nameZh;
        private String nameEn;
    }
}
