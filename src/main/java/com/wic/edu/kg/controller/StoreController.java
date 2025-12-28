package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wic.edu.kg.common.ApiResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 店铺资源控制器（RESTful）
 * 
 * RESTful 设计：
 * - GET /api/stores - 获取店铺列表
 * - GET /api/stores/{id} - 获取店铺详情
 * - POST /api/stores/{id}/likes - 点赞店铺
 * - DELETE /api/stores/{id}/likes - 取消点赞
 * 
 * 商品相关：
 * - GET /api/products/{id}/comments - 获取商品评论
 * - POST /api/products/{id}/comments - 添加评论
 * - POST /api/products/{id}/likes - 点赞商品
 */
@RestController
@Tag(name = "店铺资源", description = "校园周边美食店铺 RESTful API")
public class StoreController {

    @Autowired
    private FoodStoreService foodStoreService;

    @Autowired
    private FoodCommentService foodCommentService;

    @Autowired
    private FoodLikeService foodLikeService;

    @Autowired
    private SysUserService sysUserService;

    // ==================== 店铺接口 ====================

    @GetMapping("/api/stores")
    @Operation(summary = "获取店铺列表", description = """
            获取美食店铺列表。

            **筛选条件：**
            - location: 位置筛选
            - keyword: 关键词搜索
            - sortBy: 排序字段（rating/name）
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
    })
    public ResponseEntity<ApiResponse<Page<FoodStoreVO>>> getStores(
            @Parameter(description = "位置筛选") @RequestParam(required = false) String location,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "rating") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortOrder) {

        StoreQueryRequest request = new StoreQueryRequest();
        request.setLocation(location);
        request.setKeyword(keyword);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortOrder(sortOrder);

        Long userId = getCurrentUserId();
        Page<FoodStoreVO> result = foodStoreService.getStoreList(request, userId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/api/stores/{id}")
    @Operation(summary = "获取店铺详情", description = "获取店铺详情，包含商品列表")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "店铺不存在")
    })
    public ResponseEntity<ApiResponse<FoodStoreVO>> getStore(
            @Parameter(description = "店铺ID", required = true) @PathVariable Long id) {

        Long userId = getCurrentUserId();
        FoodStoreVO store = foodStoreService.getStoreDetail(id, userId);

        if (store == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "店铺不存在", "/api/stores/" + id));
        }

        return ResponseEntity.ok(ApiResponse.ok(store));
    }

    @PostMapping("/api/stores/{id}/likes")
    @Operation(summary = "点赞店铺", description = "为店铺点赞，需要登录")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "点赞成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> likeStore(
            @Parameter(description = "店铺ID", required = true) @PathVariable Long id) {

        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("UNAUTHORIZED", "请先登录", "/api/stores/" + id + "/likes"));
        }

        boolean isLiked = foodLikeService.toggleLike(userId, "store", id);
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("message", isLiked ? "点赞成功" : "已取消点赞");

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(result));
    }

    @DeleteMapping("/api/stores/{id}/likes")
    @Operation(summary = "取消点赞店铺", description = "取消对店铺的点赞")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "取消成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<Void> unlikeStore(
            @Parameter(description = "店铺ID", required = true) @PathVariable Long id) {

        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        foodLikeService.toggleLike(userId, "store", id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 商品点赞接口 ====================

    @PostMapping("/api/products/{id}/likes")
    @Operation(summary = "点赞商品", description = "为商品点赞，需要登录")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "点赞成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> likeProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id) {

        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("UNAUTHORIZED", "请先登录", "/api/products/" + id + "/likes"));
        }

        boolean isLiked = foodLikeService.toggleLike(userId, "product", id);
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("message", isLiked ? "点赞成功" : "已取消点赞");

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(result));
    }

    // ==================== 商品评论接口 ====================

    @GetMapping("/api/products/{productId}/comments")
    @Operation(summary = "获取商品评论", description = "分页获取商品的评论列表")
    public ResponseEntity<ApiResponse<Page<FoodCommentVO>>> getProductComments(
            @Parameter(description = "商品ID", required = true) @PathVariable Long productId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {

        Page<FoodCommentVO> result = foodCommentService.getCommentsByProductId(productId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/api/products/{productId}/comments")
    @Operation(summary = "添加商品评论", description = "为商品添加评论，需要登录")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "评论成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<ApiResponse<FoodCommentVO>> addComment(
            @Parameter(description = "商品ID", required = true) @PathVariable Long productId,
            @Validated @RequestBody CommentRequest request) {

        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("UNAUTHORIZED", "请先登录", "/api/products/" + productId + "/comments"));
        }

        FoodCommentVO comment = foodCommentService.addComment(productId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(comment));
    }

    // ==================== 辅助方法 ====================

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
