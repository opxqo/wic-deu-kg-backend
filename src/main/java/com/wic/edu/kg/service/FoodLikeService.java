package com.wic.edu.kg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wic.edu.kg.entity.FoodLike;

public interface FoodLikeService extends IService<FoodLike> {
    
    /**
     * 切换点赞状态
     * @return true=已点赞, false=已取消
     */
    boolean toggleLike(Long userId, String targetType, Long targetId);
    
    /**
     * 检查是否已点赞
     */
    boolean isLiked(Long userId, String targetType, Long targetId);
}
