package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.entity.GalleryImage;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.service.GalleryService;
import com.wic.edu.kg.service.SysUserService;
import com.wic.edu.kg.vo.GalleryImageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片库用户API（需要登录）
 */
@RestController
@RequestMapping("/api/user/gallery")
@Tag(name = "光影城院-用户", description = "用户上传和管理自己的图片")
public class GalleryUserController {

    @Autowired
    private GalleryService galleryService;

    @Autowired
    private SysUserService sysUserService;

    @Value("${spring.servlet.multipart.max-file-size:10MB}")
    private DataSize maxFileSize;

    @PostMapping("/upload")
    @Operation(summary = "上传图片", description = "用户上传图片，需要审核后才会展示")
    public Result<GalleryImage> uploadImage(
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "标题") @RequestParam String title,
            @Parameter(description = "描述") @RequestParam(required = false) String description,
            @Parameter(description = "拍摄地点") @RequestParam(required = false) String location,
            @Parameter(description = "分类") @RequestParam(defaultValue = "other") String category,
            @Parameter(description = "标签(逗号分隔)") @RequestParam(required = false) String tags) {
        
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        
        if (file == null || file.isEmpty()) {
            return Result.error("请选择图片文件");
        }
        
        // 校验文件大小
        if (file.getSize() > maxFileSize.toBytes()) {
            return Result.error("图片大小不能超过 " + maxFileSize.toMegabytes() + "MB");
        }
        
        if (title == null || title.trim().isEmpty()) {
            return Result.error("请输入图片标题");
        }
        
        GalleryImage image = galleryService.uploadImage(
                currentUser.getId(), file, title.trim(), description, location, category, tags);
        
        return Result.success(image);
    }

    @GetMapping("/my-images")
    @Operation(summary = "获取我的图片", description = "获取当前用户上传的所有图片")
    public Result<Page<GalleryImageVO>> getMyImages(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        
        Page<GalleryImageVO> result = galleryService.getUserImages(currentUser.getId(), page, size);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除我的图片", description = "删除自己上传的图片")
    public Result<Void> deleteMyImage(
            @Parameter(description = "图片ID") @PathVariable Long id) {
        
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        
        galleryService.deleteUserImage(currentUser.getId(), id);
        return Result.success();
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞/取消点赞", description = "切换图片的点赞状态")
    public Result<Integer> toggleLike(
            @Parameter(description = "图片ID") @PathVariable Long id) {
        
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

    // ==================== 工具方法 ====================

    private SysUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String studentId = auth.getName();
            return sysUserService.getByStudentId(studentId);
        }
        return null;
    }
}
