package com.wic.edu.kg.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片上传DTO
 */
@Data
public class GalleryUploadDTO {
    
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
     * 分类
     */
    private String category;
    
    /**
     * 标签(逗号分隔)
     */
    private String tags;
}
