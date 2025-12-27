package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.entity.GalleryImage;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.enums.GalleryCategory;
import com.wic.edu.kg.service.GalleryService;
import com.wic.edu.kg.service.SysUserService;
import com.wic.edu.kg.vo.GalleryImageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图片库用户API（需要登录）
 */
@RestController
@RequestMapping("/api/user/gallery")
@Tag(name = "光影城院-用户", description = "用户上传和管理自己的图片，需要登录")
public class GalleryUserController {

    @Autowired
    private GalleryService galleryService;

    @Autowired
    private SysUserService sysUserService;

    @Value("${spring.servlet.multipart.max-file-size:10MB}")
    private DataSize maxFileSize;

    // ==================== 上传和管理 ====================

    @PostMapping("/upload")
    @Operation(summary = "上传图片", description = "用户上传图片，需要审核后才会公开展示")
    @ApiResponse(responseCode = "200", description = "上传成功")
    public Result<GalleryImage> uploadImage(
            @Parameter(description = "图片文件（支持jpg/png/gif，最大10MB）", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "标题（必填）", required = true, example = "校园晨曦") @RequestParam String title,
            @Parameter(description = "描述") @RequestParam(required = false) String description,
            @Parameter(description = "拍摄地点", example = "图书馆门前") @RequestParam(required = false) String location,
            @Parameter(description = "分类（campus/activity/landscape/life/other）", example = "campus") @RequestParam(defaultValue = "other") String category,
            @Parameter(description = "标签（逗号分隔）", example = "风景,日出,校园") @RequestParam(required = false) String tags) {

        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }

        // 参数校验
        if (file == null || file.isEmpty()) {
            return Result.error("请选择图片文件");
        }
        if (file.getSize() > maxFileSize.toBytes()) {
            return Result.error("图片大小不能超过 " + maxFileSize.toMegabytes() + "MB");
        }
        if (title == null || title.trim().isEmpty()) {
            return Result.error("请输入图片标题");
        }

        // 校验分类
        if (!GalleryCategory.isValid(category) || "all".equals(category)) {
            category = "other";
        }

        GalleryImage image = galleryService.uploadImage(
                currentUser.getId(), file, title.trim(), description, location, category, tags);

        return Result.success(image);
    }

    @GetMapping("/my-images")
    @Operation(summary = "获取我的图片", description = "获取当前用户上传的所有图片")
    @ApiResponse(responseCode = "200", description = "获取成功")
    public Result<Page<GalleryImageVO>> getMyImages(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "20") @RequestParam(defaultValue = "20") int size) {

        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }

        Page<GalleryImageVO> result = galleryService.getUserImages(currentUser.getId(), page, size);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除我的图片", description = "删除自己上传的图片")
    @ApiResponse(responseCode = "200", description = "删除成功")
    public Result<Void> deleteMyImage(
            @Parameter(description = "图片ID", required = true) @PathVariable Long id) {

        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }

        galleryService.deleteUserImage(currentUser.getId(), id);
        return Result.success();
    }

    // ==================== 互动功能 ====================

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞/取消点赞", description = "切换图片的点赞状态，再次点击取消")
    @ApiResponse(responseCode = "200", description = "操作成功，返回当前点赞数")
    public Result<Integer> toggleLike(
            @Parameter(description = "图片ID", required = true) @PathVariable Long id) {

        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }

        try {
            int likeCount = galleryService.toggleLike(currentUser.getId(), id);
            return Result.success(likeCount);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/categories")
    @Operation(summary = "获取上传分类", description = "获取可用的上传分类列表（不含\"全部\"）")
    @ApiResponse(responseCode = "200", description = "获取成功")
    public Result<List<CategoryOption>> getUploadCategories() {
        List<CategoryOption> categories = Arrays.stream(GalleryCategory.values())
                .filter(c -> !"all".equals(c.getCode()))
                .map(c -> new CategoryOption(c.getCode(), c.getNameZh()))
                .collect(Collectors.toList());
        return Result.success(categories);
    }

    // ==================== 工具方法 ====================

    private SysUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String studentId = auth.getName();
            return sysUserService.getByStudentId(studentId);
        }
        return null;
    }

    /**
     * 分类选项
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class CategoryOption {
        @Parameter(description = "分类编码")
        private String code;
        @Parameter(description = "分类名称")
        private String name;
    }
}
