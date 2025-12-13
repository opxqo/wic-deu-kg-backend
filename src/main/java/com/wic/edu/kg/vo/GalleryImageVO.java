package com.wic.edu.kg.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图片VO - 用于API返回
 */
@Data
public class GalleryImageVO {
    
    private Long id;
    
    /**
     * 上传用户信息
     */
    private UserInfo uploader;
    
    /**
     * 图片标题
     */
    private String title;
    
    /**
     * 图片描述
     */
    private String description;
    
    /**
     * 拍摄地点
     */
    private String location;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 缩略图URL
     */
    private String thumbnailUrl;
    
    /**
     * 图片宽度
     */
    private Integer width;
    
    /**
     * 图片高度
     */
    private Integer height;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 标签列表
     */
    private String[] tags;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 当前用户是否已点赞
     */
    private Boolean liked;
    
    /**
     * 是否精选
     */
    private Boolean featured;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 上传者信息
     */
    @Data
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String avatar;
    }
}
