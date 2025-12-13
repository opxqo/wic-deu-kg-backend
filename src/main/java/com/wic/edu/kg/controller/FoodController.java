package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.dto.CommentRequest;
import com.wic.edu.kg.dto.StoreQueryRequest;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.service.FoodCommentService;
import com.wic.edu.kg.service.FoodLikeService;
import com.wic.edu.kg.service.FoodStoreService;
import com.wic.edu.kg.service.SysUserService;
import com.wic.edu.kg.vo.FoodCommentVO;
import com.wic.edu.kg.vo.FoodStoreVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/food")
@Tag(name = "美食管理", description = "校园周边美食店铺接口")
public class FoodController {

    @Autowired
    private FoodStoreService foodStoreService;

    @Autowired
    private FoodCommentService foodCommentService;

    @Autowired
    private FoodLikeService foodLikeService;

    @Autowired
    private SysUserService sysUserService;

    // ==================== 店铺接口 ====================

    @GetMapping("/stores")
    @Operation(summary = "获取店铺列表", description = "获取美食店铺列表，支持分页和筛选")
    public Result<Page<FoodStoreVO>> getStoreList(
            @Parameter(description = "位置筛选") @RequestParam(required = false) String location,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "rating") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        StoreQueryRequest request = new StoreQueryRequest();
        request.setLocation(location);
        request.setKeyword(keyword);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortOrder(sortOrder);
        
        Long userId = getCurrentUserId();
        return Result.success(foodStoreService.getStoreList(request, userId));
    }

    @GetMapping("/stores/{id}")
    @Operation(summary = "获取店铺详情", description = "获取店铺详情，包含商品列表")
    public Result<FoodStoreVO> getStoreDetail(
            @Parameter(description = "店铺ID") @PathVariable Long id
    ) {
        Long userId = getCurrentUserId();
        FoodStoreVO store = foodStoreService.getStoreDetail(id, userId);
        if (store == null) {
            return Result.error(404, "店铺不存在");
        }
        return Result.success(store);
    }

    @GetMapping("/stores/search")
    @Operation(summary = "搜索店铺", description = "根据关键词搜索店铺")
    public Result<Page<FoodStoreVO>> searchStores(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size
    ) {
        Long userId = getCurrentUserId();
        return Result.success(foodStoreService.searchStores(keyword, page, size, userId));
    }

    @PostMapping("/stores/{id}/like")
    @Operation(summary = "点赞/取消点赞店铺", description = "切换店铺的点赞状态，需要登录")
    public Result<Map<String, Object>> toggleStoreLike(
            @Parameter(description = "店铺ID") @PathVariable Long id
    ) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        
        boolean isLiked = foodLikeService.toggleLike(userId, "store", id);
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("message", isLiked ? "点赞成功" : "已取消点赞");
        
        return Result.success(result);
    }

    // ==================== 商品接口 ====================

    @PostMapping("/products/{id}/like")
    @Operation(summary = "点赞/取消点赞商品", description = "切换商品的点赞状态，需要登录")
    public Result<Map<String, Object>> toggleProductLike(
            @Parameter(description = "商品ID") @PathVariable Long id
    ) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        
        boolean isLiked = foodLikeService.toggleLike(userId, "product", id);
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("message", isLiked ? "点赞成功" : "已取消点赞");
        
        return Result.success(result);
    }

    // ==================== 评论接口 ====================

    @GetMapping("/products/{productId}/comments")
    @Operation(summary = "获取商品评论列表", description = "分页获取商品的评论列表")
    public Result<Page<FoodCommentVO>> getProductComments(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size
    ) {
        return Result.success(foodCommentService.getCommentsByProductId(productId, page, size));
    }

    @PostMapping("/products/{productId}/comments")
    @Operation(summary = "添加商品评论", description = "为商品添加评论，需要登录")
    public Result<FoodCommentVO> addComment(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Validated @RequestBody CommentRequest request
    ) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        
        FoodCommentVO comment = foodCommentService.addComment(productId, userId, request);
        return Result.success("评论成功", comment);
    }

    // ==================== 工具方法 ====================

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String studentId = auth.getName();
            SysUser user = sysUserService.getByStudentId(studentId);
            return user != null ? user.getId() : null;
        }
        return null;
    }
}
