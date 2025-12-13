package com.wic.edu.kg.controller;

import com.wic.edu.kg.annotation.RequireRole;
import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.dto.PageResponse;
import com.wic.edu.kg.entity.FoodComment;
import com.wic.edu.kg.entity.FoodProduct;
import com.wic.edu.kg.entity.FoodStore;
import com.wic.edu.kg.enums.UserRole;
import com.wic.edu.kg.service.FoodCommentService;
import com.wic.edu.kg.service.FoodProductService;
import com.wic.edu.kg.service.FoodStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 美食后台管理接口
 * 需要管理员或组织者权限
 */
@RestController
@RequestMapping("/api/admin/food")
@Tag(name = "美食后台管理", description = "店铺、商品、评论管理")
@RequireRole(UserRole.ORGANIZER)  // 组织者及以上权限
public class FoodAdminController {

    @Autowired
    private FoodStoreService foodStoreService;

    @Autowired
    private FoodProductService foodProductService;

    @Autowired
    private FoodCommentService foodCommentService;

    // ==================== 店铺管理 ====================

    @GetMapping("/stores")
    @Operation(summary = "分页查询店铺列表", description = "支持关键词搜索、位置筛选")
    public Result<PageResponse<FoodStore>> queryStores(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<FoodStore> result = foodStoreService.adminQueryStores(keyword, location, page, size);
        return Result.success(result);
    }

    @GetMapping("/stores/{id}")
    @Operation(summary = "获取店铺详情")
    public Result<FoodStore> getStore(@PathVariable Long id) {
        FoodStore store = foodStoreService.getById(id);
        if (store == null) {
            return Result.error(404, "店铺不存在");
        }
        return Result.success(store);
    }

    @PostMapping("/stores")
    @Operation(summary = "创建店铺")
    public Result<FoodStore> createStore(@RequestBody FoodStore store) {
        foodStoreService.save(store);
        return Result.success(store);
    }

    @PutMapping("/stores/{id}")
    @Operation(summary = "更新店铺信息")
    public Result<FoodStore> updateStore(@PathVariable Long id, @RequestBody FoodStore store) {
        FoodStore existing = foodStoreService.getById(id);
        if (existing == null) {
            return Result.error(404, "店铺不存在");
        }
        store.setId(id);
        foodStoreService.updateById(store);
        return Result.success(store);
    }

    @DeleteMapping("/stores/{id}")
    @Operation(summary = "删除店铺", description = "会同时删除店铺下的所有商品")
    public Result<Void> deleteStore(@PathVariable Long id) {
        FoodStore existing = foodStoreService.getById(id);
        if (existing == null) {
            return Result.error(404, "店铺不存在");
        }
        // 删除店铺下的所有商品
        foodProductService.deleteByStoreId(id);
        foodStoreService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/stores/batch")
    @Operation(summary = "批量删除店铺")
    public Result<Void> batchDeleteStores(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            foodProductService.deleteByStoreId(id);
        }
        foodStoreService.removeByIds(ids);
        return Result.success();
    }

    // ==================== 商品管理 ====================

    @GetMapping("/products")
    @Operation(summary = "分页查询商品列表", description = "支持店铺、分类、关键词筛选")
    public Result<PageResponse<FoodProduct>> queryProducts(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<FoodProduct> result = foodProductService.adminQueryProducts(storeId, category, keyword, status, page, size);
        return Result.success(result);
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "获取商品详情")
    public Result<FoodProduct> getProduct(@PathVariable Long id) {
        FoodProduct product = foodProductService.getById(id);
        if (product == null) {
            return Result.error(404, "商品不存在");
        }
        return Result.success(product);
    }

    @PostMapping("/products")
    @Operation(summary = "创建商品")
    public Result<FoodProduct> createProduct(@RequestBody FoodProduct product) {
        // 验证店铺存在
        FoodStore store = foodStoreService.getById(product.getStoreId());
        if (store == null) {
            return Result.error(400, "店铺不存在");
        }
        foodProductService.save(product);
        return Result.success(product);
    }

    @PutMapping("/products/{id}")
    @Operation(summary = "更新商品信息")
    public Result<FoodProduct> updateProduct(@PathVariable Long id, @RequestBody FoodProduct product) {
        FoodProduct existing = foodProductService.getById(id);
        if (existing == null) {
            return Result.error(404, "商品不存在");
        }
        product.setId(id);
        foodProductService.updateById(product);
        return Result.success(product);
    }

    @PutMapping("/products/{id}/status")
    @Operation(summary = "更新商品状态", description = "上架/下架商品")
    public Result<Void> updateProductStatus(@PathVariable Long id, @RequestParam Integer status) {
        FoodProduct existing = foodProductService.getById(id);
        if (existing == null) {
            return Result.error(404, "商品不存在");
        }
        foodProductService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "删除商品")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        FoodProduct existing = foodProductService.getById(id);
        if (existing == null) {
            return Result.error(404, "商品不存在");
        }
        // 删除商品相关的评论
        foodCommentService.deleteByProductId(id);
        foodProductService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/products/batch")
    @Operation(summary = "批量删除商品")
    public Result<Void> batchDeleteProducts(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            foodCommentService.deleteByProductId(id);
        }
        foodProductService.removeByIds(ids);
        return Result.success();
    }

    // ==================== 评论管理 ====================

    @GetMapping("/comments")
    @Operation(summary = "分页查询评论列表", description = "支持商品、用户、状态筛选")
    public Result<PageResponse<FoodComment>> queryComments(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<FoodComment> result = foodCommentService.adminQueryComments(productId, userId, status, page, size);
        return Result.success(result);
    }

    @GetMapping("/comments/{id}")
    @Operation(summary = "获取评论详情")
    public Result<FoodComment> getComment(@PathVariable Long id) {
        FoodComment comment = foodCommentService.getById(id);
        if (comment == null) {
            return Result.error(404, "评论不存在");
        }
        return Result.success(comment);
    }

    @PutMapping("/comments/{id}/status")
    @Operation(summary = "更新评论状态", description = "审核通过/隐藏评论")
    public Result<Void> updateCommentStatus(@PathVariable Long id, @RequestParam Integer status) {
        FoodComment existing = foodCommentService.getById(id);
        if (existing == null) {
            return Result.error(404, "评论不存在");
        }
        foodCommentService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/comments/{id}")
    @Operation(summary = "删除评论")
    public Result<Void> deleteComment(@PathVariable Long id) {
        FoodComment existing = foodCommentService.getById(id);
        if (existing == null) {
            return Result.error(404, "评论不存在");
        }
        foodCommentService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/comments/batch")
    @Operation(summary = "批量删除评论")
    public Result<Void> batchDeleteComments(@RequestBody List<Long> ids) {
        foodCommentService.removeByIds(ids);
        return Result.success();
    }

    // ==================== 统计接口 ====================

    @GetMapping("/stats")
    @Operation(summary = "获取美食模块统计数据")
    public Result<FoodStats> getStats() {
        FoodStats stats = new FoodStats();
        stats.setTotalStores(foodStoreService.count());
        stats.setTotalProducts(foodProductService.count());
        stats.setTotalComments(foodCommentService.count());
        stats.setActiveProducts(foodProductService.countByStatus(1));
        stats.setInactiveProducts(foodProductService.countByStatus(0));
        stats.setPendingComments(foodCommentService.countByStatus(0));
        return Result.success(stats);
    }

    /**
     * 美食模块统计数据
     */
    public static class FoodStats {
        private long totalStores;
        private long totalProducts;
        private long totalComments;
        private long activeProducts;
        private long inactiveProducts;
        private long pendingComments;

        // Getters and Setters
        public long getTotalStores() { return totalStores; }
        public void setTotalStores(long totalStores) { this.totalStores = totalStores; }
        public long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }
        public long getTotalComments() { return totalComments; }
        public void setTotalComments(long totalComments) { this.totalComments = totalComments; }
        public long getActiveProducts() { return activeProducts; }
        public void setActiveProducts(long activeProducts) { this.activeProducts = activeProducts; }
        public long getInactiveProducts() { return inactiveProducts; }
        public void setInactiveProducts(long inactiveProducts) { this.inactiveProducts = inactiveProducts; }
        public long getPendingComments() { return pendingComments; }
        public void setPendingComments(long pendingComments) { this.pendingComments = pendingComments; }
    }
}
