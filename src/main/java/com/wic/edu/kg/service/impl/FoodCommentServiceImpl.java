package com.wic.edu.kg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wic.edu.kg.dto.CommentRequest;
import com.wic.edu.kg.dto.PageResponse;
import com.wic.edu.kg.entity.FoodComment;
import com.wic.edu.kg.entity.FoodProduct;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.mapper.FoodCommentMapper;
import com.wic.edu.kg.service.FoodCommentService;
import com.wic.edu.kg.service.FoodProductService;
import com.wic.edu.kg.service.SysUserService;
import com.wic.edu.kg.vo.FoodCommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodCommentServiceImpl extends ServiceImpl<FoodCommentMapper, FoodComment> implements FoodCommentService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    @Lazy
    private FoodProductService foodProductService;

    @Override
    public Page<FoodCommentVO> getCommentsByProductId(Long productId, int page, int size) {
        Page<FoodComment> commentPage = this.page(
            new Page<>(page, size),
            new LambdaQueryWrapper<FoodComment>()
                .eq(FoodComment::getProductId, productId)
                .orderByDesc(FoodComment::getCreatedAt)
        );

        Page<FoodCommentVO> voPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        voPage.setRecords(commentPage.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList()));
        
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FoodCommentVO addComment(Long productId, Long userId, CommentRequest request) {
        FoodComment comment = new FoodComment();
        comment.setProductId(productId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setRating(request.getRating());
        comment.setLikes(0);
        comment.setDeleted(0);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        
        this.save(comment);
        
        // 更新商品的评分和评论数
        updateProductRating(productId);
        
        return convertToVO(comment);
    }

    @Override
    public FoodCommentVO getTopReview(Long productId) {
        FoodComment comment = this.getOne(new LambdaQueryWrapper<FoodComment>()
            .eq(FoodComment::getProductId, productId)
            .orderByDesc(FoodComment::getLikes)
            .orderByDesc(FoodComment::getRating)
            .last("LIMIT 1"));
        
        return comment != null ? convertToVO(comment) : null;
    }

    private void updateProductRating(Long productId) {
        // 计算平均评分
        List<FoodComment> comments = this.list(new LambdaQueryWrapper<FoodComment>()
            .eq(FoodComment::getProductId, productId));
        
        if (!comments.isEmpty()) {
            BigDecimal avgRating = comments.stream()
                .map(FoodComment::getRating)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(comments.size()), 1, RoundingMode.HALF_UP);
            
            FoodProduct product = new FoodProduct();
            product.setId(productId);
            product.setRating(avgRating);
            product.setReviewCount(comments.size());
            product.setUpdatedAt(LocalDateTime.now());
            foodProductService.updateById(product);
        }
    }

    private FoodCommentVO convertToVO(FoodComment comment) {
        FoodCommentVO vo = new FoodCommentVO();
        vo.setId(comment.getId());
        vo.setProductId(comment.getProductId());
        vo.setUserId(comment.getUserId());
        vo.setContent(comment.getContent());
        vo.setRating(comment.getRating());
        vo.setLikes(comment.getLikes());
        vo.setCreatedAt(comment.getCreatedAt());
        vo.setRelativeTime(getRelativeTime(comment.getCreatedAt()));
        
        // 获取用户信息
        SysUser user = sysUserService.getById(comment.getUserId());
        if (user != null) {
            vo.setUser(user.getUsername());
            vo.setAvatar(user.getAvatar() != null ? user.getAvatar() : "https://picsum.photos/seed/user" + user.getId() + "/40/40");
        }
        
        return vo;
    }

    private String getRelativeTime(LocalDateTime time) {
        if (time == null) return "";
        
        Duration duration = Duration.between(time, LocalDateTime.now());
        long days = duration.toDays();
        
        if (days == 0) {
            long hours = duration.toHours();
            if (hours == 0) {
                long minutes = duration.toMinutes();
                return minutes <= 1 ? "刚刚" : minutes + "分钟前";
            }
            return hours + "小时前";
        } else if (days == 1) {
            return "昨天";
        } else if (days < 7) {
            return days + "天前";
        } else if (days < 30) {
            return (days / 7) + "周前";
        } else if (days < 365) {
            return (days / 30) + "个月前";
        } else {
            return (days / 365) + "年前";
        }
    }

    // ==================== 后台管理接口实现 ====================

    @Override
    public PageResponse<FoodComment> adminQueryComments(Long productId, Long userId, Integer status, int page, int size) {
        LambdaQueryWrapper<FoodComment> wrapper = new LambdaQueryWrapper<>();
        
        // 商品筛选
        if (productId != null) {
            wrapper.eq(FoodComment::getProductId, productId);
        }
        
        // 用户筛选
        if (userId != null) {
            wrapper.eq(FoodComment::getUserId, userId);
        }
        
        // 状态筛选 (使用 deleted 字段，0-正常，1-删除/隐藏)
        if (status != null) {
            wrapper.eq(FoodComment::getDeleted, status);
        }
        
        wrapper.orderByDesc(FoodComment::getCreatedAt);
        
        Page<FoodComment> commentPage = this.page(new Page<>(page, size), wrapper);
        
        return PageResponse.<FoodComment>builder()
                .records(commentPage.getRecords())
                .total(commentPage.getTotal())
                .current(commentPage.getCurrent())
                .size(commentPage.getSize())
                .pages(commentPage.getPages())
                .build();
    }

    @Override
    public void deleteByProductId(Long productId) {
        this.remove(new LambdaQueryWrapper<FoodComment>()
                .eq(FoodComment::getProductId, productId));
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        this.update(new LambdaUpdateWrapper<FoodComment>()
                .eq(FoodComment::getId, id)
                .set(FoodComment::getDeleted, status));
    }

    @Override
    public long countByStatus(Integer status) {
        return this.count(new LambdaQueryWrapper<FoodComment>()
                .eq(FoodComment::getDeleted, status));
    }
}
