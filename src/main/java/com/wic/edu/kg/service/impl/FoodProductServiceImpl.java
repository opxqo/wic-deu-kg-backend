package com.wic.edu.kg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wic.edu.kg.dto.PageResponse;
import com.wic.edu.kg.entity.FoodProduct;
import com.wic.edu.kg.mapper.FoodProductMapper;
import com.wic.edu.kg.service.FoodLikeService;
import com.wic.edu.kg.service.FoodProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class FoodProductServiceImpl extends ServiceImpl<FoodProductMapper, FoodProduct> implements FoodProductService {

    @Autowired
    @Lazy
    private FoodLikeService foodLikeService;

    @Override
    public List<FoodProduct> getProductsByStoreId(Long storeId) {
        return this.list(new LambdaQueryWrapper<FoodProduct>()
                .eq(FoodProduct::getStoreId, storeId)
                .eq(FoodProduct::getStatus, 1)
                .orderByDesc(FoodProduct::getRating));
    }

    @Override
    public FoodProduct getProductDetail(Long productId, Long userId) {
        return this.getById(productId);
    }

    // ==================== 后台管理接口实现 ====================

    @Override
    public PageResponse<FoodProduct> adminQueryProducts(Long storeId, String category, String keyword, Integer status, int page, int size) {
        LambdaQueryWrapper<FoodProduct> wrapper = new LambdaQueryWrapper<>();
        
        // 店铺筛选
        if (storeId != null) {
            wrapper.eq(FoodProduct::getStoreId, storeId);
        }
        
        // 分类筛选
        if (StringUtils.hasText(category)) {
            wrapper.eq(FoodProduct::getCategory, category);
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(FoodProduct::getStatus, status);
        }
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(FoodProduct::getName, keyword)
                .or()
                .like(FoodProduct::getDescription, keyword));
        }
        
        wrapper.orderByDesc(FoodProduct::getCreatedAt);
        
        Page<FoodProduct> productPage = this.page(new Page<>(page, size), wrapper);
        
        return PageResponse.<FoodProduct>builder()
                .records(productPage.getRecords())
                .total(productPage.getTotal())
                .current(productPage.getCurrent())
                .size(productPage.getSize())
                .pages(productPage.getPages())
                .build();
    }

    @Override
    public void deleteByStoreId(Long storeId) {
        this.remove(new LambdaQueryWrapper<FoodProduct>()
                .eq(FoodProduct::getStoreId, storeId));
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        this.update(new LambdaUpdateWrapper<FoodProduct>()
                .eq(FoodProduct::getId, id)
                .set(FoodProduct::getStatus, status));
    }

    @Override
    public long countByStatus(Integer status) {
        return this.count(new LambdaQueryWrapper<FoodProduct>()
                .eq(FoodProduct::getStatus, status));
    }
}
