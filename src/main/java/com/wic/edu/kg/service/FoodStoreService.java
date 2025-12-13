package com.wic.edu.kg.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wic.edu.kg.dto.PageResponse;
import com.wic.edu.kg.dto.StoreQueryRequest;
import com.wic.edu.kg.entity.FoodStore;
import com.wic.edu.kg.vo.FoodStoreVO;

public interface FoodStoreService extends IService<FoodStore> {
    
    /**
     * 获取店铺列表（带分页、筛选）
     */
    Page<FoodStoreVO> getStoreList(StoreQueryRequest request, Long userId);
    
    /**
     * 获取店铺详情（含商品列表）
     */
    FoodStoreVO getStoreDetail(Long storeId, Long userId);
    
    /**
     * 搜索店铺
     */
    Page<FoodStoreVO> searchStores(String keyword, int page, int size, Long userId);

    // ==================== 后台管理接口 ====================

    /**
     * 后台查询店铺列表
     */
    PageResponse<FoodStore> adminQueryStores(String keyword, String location, int page, int size);
}
