package com.wic.edu.kg.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wic.edu.kg.dto.CommentRequest;
import com.wic.edu.kg.dto.PageResponse;
import com.wic.edu.kg.entity.FoodComment;
import com.wic.edu.kg.vo.FoodCommentVO;

public interface FoodCommentService extends IService<FoodComment> {
    
    /**
     * 获取商品评论列表（分页）
     */
    Page<FoodCommentVO> getCommentsByProductId(Long productId, int page, int size);
    
    /**
     * 添加评论
     */
    FoodCommentVO addComment(Long productId, Long userId, CommentRequest request);
    
    /**
     * 获取商品的最新精选评论
     */
    FoodCommentVO getTopReview(Long productId);

    // ==================== 后台管理接口 ====================

    /**
     * 后台查询评论列表
     */
    PageResponse<FoodComment> adminQueryComments(Long productId, Long userId, Integer status, int page, int size);

    /**
     * 删除商品下的所有评论
     */
    void deleteByProductId(Long productId);

    /**
     * 更新评论状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 按状态统计评论数量
     */
    long countByStatus(Integer status);
}
