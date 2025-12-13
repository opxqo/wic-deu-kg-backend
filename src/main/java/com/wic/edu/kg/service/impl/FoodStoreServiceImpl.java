package com.wic.edu.kg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wic.edu.kg.dto.PageResponse;
import com.wic.edu.kg.dto.StoreQueryRequest;
import com.wic.edu.kg.entity.FoodProduct;
import com.wic.edu.kg.entity.FoodStore;
import com.wic.edu.kg.mapper.FoodStoreMapper;
import com.wic.edu.kg.service.FoodCommentService;
import com.wic.edu.kg.service.FoodLikeService;
import com.wic.edu.kg.service.FoodProductService;
import com.wic.edu.kg.service.FoodStoreService;
import com.wic.edu.kg.vo.FoodCommentVO;
import com.wic.edu.kg.vo.FoodProductVO;
import com.wic.edu.kg.vo.FoodStoreVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodStoreServiceImpl extends ServiceImpl<FoodStoreMapper, FoodStore> implements FoodStoreService {

    @Autowired
    @Lazy
    private FoodProductService foodProductService;

    @Autowired
    @Lazy
    private FoodLikeService foodLikeService;

    @Autowired
    @Lazy
    private FoodCommentService foodCommentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Page<FoodStoreVO> getStoreList(StoreQueryRequest request, Long userId) {
        LambdaQueryWrapper<FoodStore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FoodStore::getStatus, 1);
        
        // 位置筛选
        if (StringUtils.hasText(request.getLocation()) && !"All".equals(request.getLocation())) {
            wrapper.eq(FoodStore::getLocation, request.getLocation());
        }
        
        // 关键词搜索
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w
                .like(FoodStore::getName, request.getKeyword())
                .or()
                .like(FoodStore::getDescription, request.getKeyword())
                .or()
                .like(FoodStore::getTags, request.getKeyword()));
        }
        
        // 排序
        String sortBy = request.getSortBy();
        boolean isAsc = "asc".equalsIgnoreCase(request.getSortOrder());
        if ("rating".equals(sortBy)) {
            wrapper.orderBy(true, isAsc, FoodStore::getRating);
        } else if ("likes".equals(sortBy)) {
            wrapper.orderBy(true, isAsc, FoodStore::getLikes);
        } else if ("reviewCount".equals(sortBy)) {
            wrapper.orderBy(true, isAsc, FoodStore::getReviewCount);
        } else {
            wrapper.orderByDesc(FoodStore::getRating);
        }
        
        Page<FoodStore> storePage = this.page(new Page<>(request.getPage(), request.getSize()), wrapper);
        
        Page<FoodStoreVO> voPage = new Page<>(storePage.getCurrent(), storePage.getSize(), storePage.getTotal());
        voPage.setRecords(storePage.getRecords().stream()
            .map(store -> convertToVO(store, userId, false))
            .collect(Collectors.toList()));
        
        return voPage;
    }

    @Override
    public FoodStoreVO getStoreDetail(Long storeId, Long userId) {
        FoodStore store = this.getById(storeId);
        if (store == null) {
            return null;
        }
        return convertToVO(store, userId, true);
    }

    @Override
    public Page<FoodStoreVO> searchStores(String keyword, int page, int size, Long userId) {
        StoreQueryRequest request = new StoreQueryRequest();
        request.setKeyword(keyword);
        request.setPage(page);
        request.setSize(size);
        return getStoreList(request, userId);
    }

    private FoodStoreVO convertToVO(FoodStore store, Long userId, boolean includeProducts) {
        FoodStoreVO vo = new FoodStoreVO();
        vo.setId(store.getId());
        vo.setName(store.getName());
        vo.setLocation(store.getLocation());
        vo.setDescription(store.getDescription());
        vo.setImage(store.getImage());
        vo.setRating(store.getRating());
        vo.setReviewCount(store.getReviewCount());
        vo.setLikes(store.getLikes());
        vo.setBusinessHours(store.getBusinessHours());
        vo.setContact(store.getContact());
        
        // 解析标签
        vo.setTags(parseTags(store.getTags()));
        
        // 检查是否已点赞
        vo.setIsLiked(foodLikeService.isLiked(userId, "store", store.getId()));
        
        if (includeProducts) {
            // 获取商品列表
            List<FoodProduct> products = foodProductService.getProductsByStoreId(store.getId());
            vo.setProducts(products.stream()
                .map(p -> convertProductToVO(p, userId))
                .collect(Collectors.toList()));
            
            // 计算人均价格
            if (!products.isEmpty()) {
                BigDecimal avgPrice = products.stream()
                    .map(FoodProduct::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(products.size()), 0, RoundingMode.HALF_UP);
                vo.setAvgPrice(avgPrice);
            }
        }
        
        return vo;
    }

    private FoodProductVO convertProductToVO(FoodProduct product, Long userId) {
        FoodProductVO vo = new FoodProductVO();
        vo.setId(product.getId());
        vo.setStoreId(product.getStoreId());
        vo.setName(product.getName());
        vo.setDescription(product.getDescription());
        vo.setImage(product.getImage());
        vo.setPrice(product.getPrice());
        vo.setRating(product.getRating());
        vo.setReviewCount(product.getReviewCount());
        vo.setLikes(product.getLikes());
        vo.setCategory(product.getCategory());
        vo.setSpiciness(product.getSpiciness());
        
        // 解析标签
        vo.setTags(parseTags(product.getTags()));
        
        // 检查是否已点赞
        vo.setIsLiked(foodLikeService.isLiked(userId, "product", product.getId()));
        
        // 获取精选评论
        FoodCommentVO topComment = foodCommentService.getTopReview(product.getId());
        if (topComment != null) {
            FoodProductVO.TopReviewVO topReview = new FoodProductVO.TopReviewVO();
            topReview.setUser(topComment.getUser());
            topReview.setAvatar(topComment.getAvatar());
            topReview.setContent(topComment.getContent());
            vo.setTopReview(topReview);
        }
        
        return vo;
    }

    private List<String> parseTags(String tagsJson) {
        if (!StringUtils.hasText(tagsJson)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            // 如果不是JSON格式，尝试按逗号分隔
            return List.of(tagsJson.split(","));
        }
    }

    // ==================== 后台管理接口实现 ====================

    @Override
    public PageResponse<FoodStore> adminQueryStores(String keyword, String location, int page, int size) {
        LambdaQueryWrapper<FoodStore> wrapper = new LambdaQueryWrapper<>();
        
        // 位置筛选
        if (StringUtils.hasText(location)) {
            wrapper.eq(FoodStore::getLocation, location);
        }
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(FoodStore::getName, keyword)
                .or()
                .like(FoodStore::getDescription, keyword));
        }
        
        wrapper.orderByDesc(FoodStore::getCreatedAt);
        
        Page<FoodStore> storePage = this.page(new Page<>(page, size), wrapper);
        
        return PageResponse.<FoodStore>builder()
                .records(storePage.getRecords())
                .total(storePage.getTotal())
                .current(storePage.getCurrent())
                .size(storePage.getSize())
                .pages(storePage.getPages())
                .build();
    }
}
