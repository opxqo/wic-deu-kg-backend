package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wic.edu.kg.common.ApiResponse;
import com.wic.edu.kg.entity.GalleryImage;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.enums.GalleryCategory;
import com.wic.edu.kg.service.GalleryService;
import com.wic.edu.kg.service.SysUserService;
import com.wic.edu.kg.vo.GalleryImageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图片资源控制器（RESTful）
 * 
 * RESTful 设计：
 * - GET /api/images - 获取图片列表
 * - GET /api/images/{id} - 获取图片详情
 * - POST /api/images - 上传图片
 * - DELETE /api/images/{id} - 删除图片
 * - POST /api/images/{id}/likes - 点赞
 * - DELETE /api/images/{id}/likes - 取消点赞
 * - GET /api/image-categories - 获取分类列表
 */
@RestController
@Tag(name = "图片资源", description = "校园图片库 RESTful API")
public class ImageController {

    @Autowired
    private GalleryService galleryService;

    @Autowired
    private SysUserService sysUserService;

    @Value("${spring.servlet.multipart.max-file-size:10MB}")
    private DataSize maxFileSize;

    // ==================== 公开接口 ====================

    @GetMapping("/api/images")
    @Operation(summary = "获取图片列表", description = """
            获取已审核通过的图片列表。

            **筛选条件：**
            - category: 分类筛选
            - keyword: 关键词搜索
            - featured: 是否只看精选
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
    })
    public ResponseEntity<ApiResponse<Page<GalleryImageVO>>> getImages(
            @Parameter(description = "分类筛选") @RequestParam(required = false) String category,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "是否只看精选") @RequestParam(required = false, defaultValue = "false") boolean featured,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal SysUser currentUser) {

        Long userId = currentUser != null ? currentUser.getId() : null;

        if (featured) {
            List<GalleryImageVO> featuredList = galleryService.getFeaturedImages(size);
            Page<GalleryImageVO> result = new Page<>(page, size);
            result.setRecords(featuredList);
            result.setTotal(featuredList.size());
            return ResponseEntity.ok(ApiResponse.ok(result));
        }

        Page<GalleryImageVO> result = galleryService.getApprovedImages(category, keyword, page, size, userId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/api/images/{id}")
    @Operation(summary = "获取图片详情", description = "获取单张图片的详细信息，同时增加浏览量")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "图片不存在")
    })
    public ResponseEntity<ApiResponse<GalleryImageVO>> getImage(
            @Parameter(description = "图片ID", required = true) @PathVariable Long id,
            @AuthenticationPrincipal SysUser currentUser) {

        Long userId = currentUser != null ? currentUser.getId() : null;
        GalleryImageVO image = galleryService.getImageDetail(id, userId);

        if (image == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "图片不存在", "/api/images/" + id));
        }

        galleryService.incrementViewCount(id);
        return ResponseEntity.ok(ApiResponse.ok(image));
    }

    @GetMapping("/api/image-categories")
    @Operation(summary = "获取分类列表", description = "获取所有可用的图片分类")
    public ResponseEntity<ApiResponse<List<CategoryInfo>>> getCategories() {
        List<CategoryInfo> categories = Arrays.stream(GalleryCategory.values())
                .map(c -> new CategoryInfo(c.getCode(), c.getNameZh(), c.getNameEn()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }

    // ==================== 需认证接口 ====================

    @PostMapping(value = "/api/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传图片", description = """
            上传新图片到图库。

            **上传规则：**
            - 支持 jpg/png/gif 格式
            - 最大 10MB
            - 需要审核后才会公开展示
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "上传成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误或文件过大"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<ApiResponse<GalleryImage>> uploadImage(
            @Parameter(description = "图片文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "标题", required = true) @RequestParam String title,
            @Parameter(description = "描述") @RequestParam(required = false) String description,
            @Parameter(description = "拍摄地点") @RequestParam(required = false) String location,
            @Parameter(description = "分类") @RequestParam(defaultValue = "other") String category,
            @Parameter(description = "标签（逗号分隔）") @RequestParam(required = false) String tags) {

        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("UNAUTHORIZED", "请先登录", "/api/images"));
        }

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("BAD_REQUEST", "请选择图片文件", "/api/images"));
        }

        if (file.getSize() > maxFileSize.toBytes()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("FILE_TOO_LARGE", "图片大小不能超过 " + maxFileSize.toMegabytes() + "MB",
                            "/api/images"));
        }

        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("BAD_REQUEST", "请输入图片标题", "/api/images"));
        }

        if (!GalleryCategory.isValid(category) || "all".equals(category)) {
            category = "other";
        }

        GalleryImage image = galleryService.uploadImage(
                currentUser.getId(), file, title.trim(), description, location, category, tags);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(image));
    }

    @DeleteMapping("/api/images/{id}")
    @Operation(summary = "删除图片", description = "删除自己上传的图片")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权删除该图片"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "图片不存在")
    })
    public ResponseEntity<Void> deleteImage(
            @Parameter(description = "图片ID", required = true) @PathVariable Long id) {

        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        galleryService.deleteUserImage(currentUser.getId(), id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 点赞接口 ====================

    @PostMapping("/api/images/{id}/likes")
    @Operation(summary = "点赞图片", description = "为图片点赞")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "点赞成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "图片不存在")
    })
    public ResponseEntity<ApiResponse<Integer>> likeImage(
            @Parameter(description = "图片ID", required = true) @PathVariable Long id) {

        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("UNAUTHORIZED", "请先登录", "/api/images/" + id + "/likes"));
        }

        int likeCount = galleryService.toggleLike(currentUser.getId(), id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(likeCount));
    }

    @DeleteMapping("/api/images/{id}/likes")
    @Operation(summary = "取消点赞", description = "取消对图片的点赞")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "取消成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<Void> unlikeImage(
            @Parameter(description = "图片ID", required = true) @PathVariable Long id) {

        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // toggleLike 会自动切换状态
        galleryService.toggleLike(currentUser.getId(), id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 用户图片管理 ====================

    @GetMapping("/api/users/me/images")
    @Operation(summary = "获取我的图片", description = "获取当前用户上传的所有图片")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<ApiResponse<Page<GalleryImageVO>>> getMyImages(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {

        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("UNAUTHORIZED", "请先登录", "/api/users/me/images"));
        }

        Page<GalleryImageVO> result = galleryService.getUserImages(currentUser.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // ==================== 辅助方法和VO ====================

    private SysUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String studentId = auth.getName();
            return sysUserService.getByStudentId(studentId);
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    public static class CategoryInfo {
        @Schema(description = "分类编码")
        private String code;
        @Schema(description = "中文名称")
        private String nameZh;
        @Schema(description = "英文名称")
        private String nameEn;
    }
}
