package com.wic.edu.kg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wic.edu.kg.entity.FoodLike;
import com.wic.edu.kg.entity.FoodProduct;
import com.wic.edu.kg.entity.FoodStore;
import com.wic.edu.kg.mapper.FoodLikeMapper;
import com.wic.edu.kg.service.FoodLikeService;
import com.wic.edu.kg.service.FoodProductService;
import com.wic.edu.kg.service.FoodStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FoodLikeServiceImpl extends ServiceImpl<FoodLikeMapper, FoodLike> implements FoodLikeService {

    @Autowired
    @Lazy
    private FoodStoreService foodStoreService;

    @Autowired
    @Lazy
    private FoodProductService foodProductService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleLike(Long userId, String targetType, Long targetId) {
        FoodLike existing = this.getOne(new LambdaQueryWrapper<FoodLike>()
            .eq(FoodLike::getUserId, userId)
            .eq(FoodLike::getTargetType, targetType)
            .eq(FoodLike::getTargetId, targetId));
        
        if (existing != null) {
            // 取消点赞
            this.removeById(existing.getId());
            updateLikeCount(targetType, targetId, -1);
            return false;
        } else {
            // 添加点赞
            FoodLike like = new FoodLike();
            like.setUserId(userId);
            like.setTargetType(targetType);
            like.setTargetId(targetId);
            like.setCreatedAt(LocalDateTime.now());
            this.save(like);
            updateLikeCount(targetType, targetId, 1);
            return true;
        }
    }

    @Override
    public boolean isLiked(Long userId, String targetType, Long targetId) {
        if (userId == null) return false;
        
        return this.count(new LambdaQueryWrapper<FoodLike>()
            .eq(FoodLike::getUserId, userId)
            .eq(FoodLike::getTargetType, targetType)
            .eq(FoodLike::getTargetId, targetId)) > 0;
    }

    private void updateLikeCount(String targetType, Long targetId, int delta) {
        if ("store".equals(targetType)) {
            FoodStore store = foodStoreService.getById(targetId);
            if (store != null) {
                store.setLikes(Math.max(0, (store.getLikes() != null ? store.getLikes() : 0) + delta));
                store.setUpdatedAt(LocalDateTime.now());
                foodStoreService.updateById(store);
            }
        } else if ("product".equals(targetType)) {
            FoodProduct product = foodProductService.getById(targetId);
            if (product != null) {
                product.setLikes(Math.max(0, (product.getLikes() != null ? product.getLikes() : 0) + delta));
                product.setUpdatedAt(LocalDateTime.now());
                foodProductService.updateById(product);
            }
        }
    }
}
