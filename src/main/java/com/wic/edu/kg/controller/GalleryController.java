package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.enums.GalleryCategory;
import com.wic.edu.kg.service.GalleryService;
import com.wic.edu.kg.vo.GalleryImageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图片库公开API
 */
@RestController
@RequestMapping("/api/gallery")
@Tag(name = "光影城院", description = "校园图片库公开接口，提供图片浏览、搜索、点赞等功能")
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    @GetMapping
    @Operation(summary = "获取图片列表", description = "获取已审核通过的图片列表，支持分类筛选和关键词搜索")
    @ApiResponse(responseCode = "200", description = "获取成功")
    public Result<Page<GalleryImageVO>> getImages(
            @Parameter(description = "分类筛选（campus/activity/landscape/life/other）") @RequestParam(required = false) String category,
            @Parameter(description = "搜索关键词（标题/描述/地点）") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "20") @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal SysUser currentUser) {

        Long userId = currentUser != null ? currentUser.getId() : null;
        Page<GalleryImageVO> result = galleryService.getApprovedImages(category, keyword, page, size, userId);
        return Result.success(result);
    }

    @GetMapping("/featured")
    @Operation(summary = "获取精选图片", description = "获取精选图片列表，用于首页展示")
    @ApiResponse(responseCode = "200", description = "获取成功")
    public Result<List<GalleryImageVO>> getFeaturedImages(
            @Parameter(description = "数量限制", example = "8") @RequestParam(defaultValue = "8") int limit) {
        List<GalleryImageVO> images = galleryService.getFeaturedImages(limit);
        return Result.success(images);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取图片详情", description = "获取单张图片的详细信息")
    @ApiResponse(responseCode = "200", description = "获取成功")
    public Result<GalleryImageVO> getImageDetail(
            @Parameter(description = "图片ID", required = true) @PathVariable Long id,
            @AuthenticationPrincipal SysUser currentUser) {

        Long userId = currentUser != null ? currentUser.getId() : null;
        GalleryImageVO image = galleryService.getImageDetail(id, userId);
        if (image == null) {
            return Result.error("图片不存在");
        }
        galleryService.incrementViewCount(id);
        return Result.success(image);
    }

    @GetMapping("/categories")
    @Operation(summary = "获取分类列表", description = "获取所有可用的图片分类")
    @ApiResponse(responseCode = "200", description = "获取成功")
    public Result<List<CategoryInfo>> getCategories() {
        List<CategoryInfo> categories = Arrays.stream(GalleryCategory.values())
                .map(c -> new CategoryInfo(c.getCode(), c.getNameZh(), c.getNameEn()))
                .collect(Collectors.toList());
        return Result.success(categories);
    }

    /**
     * 分类信息VO
     */
    @Data
    @AllArgsConstructor
    public static class CategoryInfo {
        @Parameter(description = "分类编码")
        private String code;
        @Parameter(description = "中文名称")
        private String nameZh;
        @Parameter(description = "英文名称")
        private String nameEn;
    }
}
