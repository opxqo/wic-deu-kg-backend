package com.wic.edu.kg.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * R2 存储服务接口
 */
public interface R2StorageService {

    /**
     * 上传文件
     * 
     * @param file   文件
     * @param folder 文件夹路径 (如 "gallery", "avatar")
     * @return 文件URL
     */
    String uploadFile(MultipartFile file, String folder);

    /**
     * 上传文件并生成缩略图
     * 
     * @param file   图片文件
     * @param folder 文件夹路径
     * @return [原图URL, 缩略图URL]
     */
    String[] uploadImageWithThumbnail(MultipartFile file, String folder);

    /**
     * 删除文件
     * 
     * @param fileUrl 文件URL
     * @return 是否成功
     */
    boolean deleteFile(String fileUrl);

    /**
     * 从URL提取文件key
     * 
     * @param fileUrl 文件URL
     * @return 文件key
     */
    String extractKeyFromUrl(String fileUrl);

    /**
     * 上传字节数组文件
     * 
     * @param data        文件数据
     * @param folder      文件夹路径
     * @param filename    文件名
     * @param contentType 内容类型
     * @return 文件URL
     */
    String uploadBytes(byte[] data, String folder, String filename, String contentType);
}
