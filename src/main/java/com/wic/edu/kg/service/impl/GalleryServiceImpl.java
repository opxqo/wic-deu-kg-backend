package com.wic.edu.kg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wic.edu.kg.entity.GalleryImage;
import com.wic.edu.kg.entity.GalleryLike;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.mapper.GalleryImageMapper;
import com.wic.edu.kg.mapper.GalleryLikeMapper;
import com.wic.edu.kg.mapper.SysUserMapper;
import com.wic.edu.kg.service.GalleryService;
import com.wic.edu.kg.service.R2StorageService;
import com.wic.edu.kg.vo.GalleryImageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 图片库服务实现
 */
@Slf4j
@Service
public class GalleryServiceImpl extends ServiceImpl<GalleryImageMapper, GalleryImage> implements GalleryService {

    @Autowired
    private GalleryLikeMapper likeMapper;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private R2StorageService r2StorageService;

    @Override
    public Page<GalleryImageVO> getApprovedImages(String category, int page, int size, Long currentUserId) {
        Page<GalleryImage> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<GalleryImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GalleryImage::getStatus, 1); // 已审核通过
        if (category != null && !category.isEmpty() && !"all".equals(category)) {
            wrapper.eq(GalleryImage::getCategory, category);
        }
        wrapper.orderByDesc(GalleryImage::getSortOrder)
               .orderByDesc(GalleryImage::getCreatedAt);
        
        Page<GalleryImage> result = this.page(pageParam, wrapper);
        
        // 获取当前用户点赞的图片ID集合
        Set<Long> likedImageIds = getLikedImageIds(currentUserId, 
                result.getRecords().stream().map(GalleryImage::getId).collect(Collectors.toList()));
        
        // 转换为VO
        Page<GalleryImageVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(img -> convertToVO(img, likedImageIds.contains(img.getId())))
                .collect(Collectors.toList()));
        
        return voPage;
    }

    @Override
    public List<GalleryImageVO> getFeaturedImages(int limit) {
        LambdaQueryWrapper<GalleryImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GalleryImage::getStatus, 1)
               .eq(GalleryImage::getIsFeatured, 1)
               .orderByDesc(GalleryImage::getSortOrder)
               .last("LIMIT " + limit);
        
        List<GalleryImage> images = this.list(wrapper);
        return images.stream()
                .map(img -> convertToVO(img, false))
                .collect(Collectors.toList());
    }

    @Override
    public GalleryImageVO getImageDetail(Long id, Long currentUserId) {
        GalleryImage image = this.getById(id);
        if (image == null || image.getStatus() != 1) {
            return null;
        }
        
        boolean liked = false;
        if (currentUserId != null) {
            LambdaQueryWrapper<GalleryLike> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(GalleryLike::getImageId, id)
                   .eq(GalleryLike::getUserId, currentUserId);
            liked = likeMapper.selectCount(wrapper) > 0;
        }
        
        return convertToVO(image, liked);
    }

    @Override
    @Transactional
    public GalleryImage uploadImage(Long userId, MultipartFile file, String title, String description,
                                   String location, String category, String tags) {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只支持上传图片文件");
        }
        
        // 验证文件大小 (最大10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("图片大小不能超过10MB");
        }
        
        // 上传到R2
        String[] urls = r2StorageService.uploadImageWithThumbnail(file, "gallery");
        String imageUrl = urls[0];
        String thumbnailUrl = urls[1];
        
        // 获取图片尺寸
        Integer width = null, height = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage != null) {
                width = bufferedImage.getWidth();
                height = bufferedImage.getHeight();
            }
        } catch (Exception e) {
            log.warn("Failed to read image dimensions: {}", e.getMessage());
        }
        
        // 创建记录
        GalleryImage image = new GalleryImage();
        image.setUserId(userId);
        image.setTitle(title);
        image.setDescription(description);
        image.setLocation(location);
        image.setImageUrl(imageUrl);
        image.setThumbnailUrl(thumbnailUrl);
        image.setWidth(width);
        image.setHeight(height);
        image.setFileSize(file.getSize());
        image.setFileType(contentType);
        image.setCategory(category != null ? category : "other");
        image.setTags(tags);
        image.setViewCount(0);
        image.setLikeCount(0);
        image.setStatus(0); // 待审核
        image.setIsFeatured(0);
        image.setSortOrder(0);
        image.setCreatedAt(LocalDateTime.now());
        image.setUpdatedAt(LocalDateTime.now());
        
        this.save(image);
        return image;
    }

    @Override
    public Page<GalleryImageVO> getUserImages(Long userId, int page, int size) {
        Page<GalleryImage> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<GalleryImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GalleryImage::getUserId, userId)
               .orderByDesc(GalleryImage::getCreatedAt);
        
        Page<GalleryImage> result = this.page(pageParam, wrapper);
        
        Page<GalleryImageVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(img -> convertToVO(img, false))
                .collect(Collectors.toList()));
        
        return voPage;
    }

    @Override
    @Transactional
    public void deleteUserImage(Long userId, Long imageId) {
        GalleryImage image = this.getById(imageId);
        if (image == null) {
            throw new RuntimeException("图片不存在");
        }
        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此图片");
        }
        
        // 删除R2文件
        if (image.getImageUrl() != null) {
            r2StorageService.deleteFile(image.getImageUrl());
        }
        if (image.getThumbnailUrl() != null) {
            r2StorageService.deleteFile(image.getThumbnailUrl());
        }
        
        // 删除点赞记录
        LambdaQueryWrapper<GalleryLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(GalleryLike::getImageId, imageId);
        likeMapper.delete(likeWrapper);
        
        // 删除图片记录
        this.removeById(imageId);
    }

    @Override
    @Transactional
    public int toggleLike(Long userId, Long imageId) {
        GalleryImage image = this.getById(imageId);
        if (image == null || image.getStatus() != 1) {
            throw new RuntimeException("图片不存在或未审核");
        }
        
        LambdaQueryWrapper<GalleryLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GalleryLike::getImageId, imageId)
               .eq(GalleryLike::getUserId, userId);
        
        GalleryLike existingLike = likeMapper.selectOne(wrapper);
        
        if (existingLike != null) {
            // 取消点赞
            likeMapper.deleteById(existingLike.getId());
            // 减少点赞数
            LambdaUpdateWrapper<GalleryImage> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(GalleryImage::getId, imageId)
                        .setSql("like_count = like_count - 1");
            this.update(updateWrapper);
            return image.getLikeCount() - 1;
        } else {
            // 添加点赞
            GalleryLike like = new GalleryLike();
            like.setImageId(imageId);
            like.setUserId(userId);
            like.setCreatedAt(LocalDateTime.now());
            likeMapper.insert(like);
            // 增加点赞数
            LambdaUpdateWrapper<GalleryImage> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(GalleryImage::getId, imageId)
                        .setSql("like_count = like_count + 1");
            this.update(updateWrapper);
            return image.getLikeCount() + 1;
        }
    }

    @Override
    public void incrementViewCount(Long imageId) {
        LambdaUpdateWrapper<GalleryImage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(GalleryImage::getId, imageId)
                    .setSql("view_count = view_count + 1");
        this.update(updateWrapper);
    }

    // ========== 管理接口实现 ==========

    @Override
    public Page<GalleryImage> getAllImages(Integer status, int page, int size) {
        Page<GalleryImage> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<GalleryImage> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(GalleryImage::getStatus, status);
        }
        wrapper.orderByDesc(GalleryImage::getCreatedAt);
        
        return this.page(pageParam, wrapper);
    }

    @Override
    @Transactional
    public void reviewImage(Long imageId, boolean approved, String rejectReason, Long reviewerId) {
        GalleryImage image = this.getById(imageId);
        if (image == null) {
            throw new RuntimeException("图片不存在");
        }
        
        image.setStatus(approved ? 1 : 2);
        image.setRejectReason(approved ? null : rejectReason);
        image.setReviewedBy(reviewerId);
        image.setReviewedAt(LocalDateTime.now());
        image.setUpdatedAt(LocalDateTime.now());
        
        this.updateById(image);
    }

    @Override
    public void setFeatured(Long imageId, boolean featured) {
        GalleryImage image = this.getById(imageId);
        if (image == null) {
            throw new RuntimeException("图片不存在");
        }
        
        image.setIsFeatured(featured ? 1 : 0);
        image.setUpdatedAt(LocalDateTime.now());
        this.updateById(image);
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        GalleryImage image = this.getById(imageId);
        if (image == null) {
            return;
        }
        
        // 删除R2文件
        if (image.getImageUrl() != null) {
            r2StorageService.deleteFile(image.getImageUrl());
        }
        if (image.getThumbnailUrl() != null) {
            r2StorageService.deleteFile(image.getThumbnailUrl());
        }
        
        // 删除点赞记录
        LambdaQueryWrapper<GalleryLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(GalleryLike::getImageId, imageId);
        likeMapper.delete(likeWrapper);
        
        // 删除图片记录
        this.removeById(imageId);
    }

    // ========== 辅助方法 ==========

    /**
     * 获取用户点赞的图片ID集合
     */
    private Set<Long> getLikedImageIds(Long userId, List<Long> imageIds) {
        if (userId == null || imageIds.isEmpty()) {
            return Set.of();
        }
        
        LambdaQueryWrapper<GalleryLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GalleryLike::getUserId, userId)
               .in(GalleryLike::getImageId, imageIds);
        
        return likeMapper.selectList(wrapper).stream()
                .map(GalleryLike::getImageId)
                .collect(Collectors.toSet());
    }

    /**
     * 转换为VO
     */
    private GalleryImageVO convertToVO(GalleryImage image, boolean liked) {
        GalleryImageVO vo = new GalleryImageVO();
        vo.setId(image.getId());
        vo.setTitle(image.getTitle());
        vo.setDescription(image.getDescription());
        vo.setLocation(image.getLocation());
        vo.setImageUrl(image.getImageUrl());
        vo.setThumbnailUrl(image.getThumbnailUrl());
        vo.setWidth(image.getWidth());
        vo.setHeight(image.getHeight());
        vo.setCategory(image.getCategory());
        vo.setTags(image.getTags() != null ? image.getTags().split(",") : new String[0]);
        vo.setViewCount(image.getViewCount());
        vo.setLikeCount(image.getLikeCount());
        vo.setLiked(liked);
        vo.setFeatured(image.getIsFeatured() == 1);
        vo.setCreatedAt(image.getCreatedAt());
        
        // 获取上传者信息
        SysUser user = userMapper.selectById(image.getUserId());
        if (user != null) {
            GalleryImageVO.UserInfo userInfo = new GalleryImageVO.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setNickname(user.getName() != null ? user.getName() : user.getUsername());
            userInfo.setAvatar(user.getAvatar());
            vo.setUploader(userInfo);
        }
        
        return vo;
    }
}
