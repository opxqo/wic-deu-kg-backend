package com.wic.edu.kg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wic.edu.kg.dto.PageResponse;
import com.wic.edu.kg.entity.FoodProduct;

import java.util.List;

public interface FoodProductService extends IService<FoodProduct> {
    
    /**
     * 获取店铺的商品列表
     */
    List<FoodProduct> getProductsByStoreId(Long storeId);
    
    /**
     * 获取商品详情（含是否点赞）
     */
    FoodProduct getProductDetail(Long productId, Long userId);

    // ==================== 后台管理接口 ====================

    /**
     * 后台查询商品列表
     */
    PageResponse<FoodProduct> adminQueryProducts(Long storeId, String category, String keyword, Integer status, int page, int size);

    /**
     * 删除店铺下的所有商品
     */
    void deleteByStoreId(Long storeId);

    /**
     * 更新商品状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 按状态统计商品数量
     */
    long countByStatus(Integer status);
}
