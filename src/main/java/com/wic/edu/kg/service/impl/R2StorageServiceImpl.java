package com.wic.edu.kg.service.impl;

import com.wic.edu.kg.service.R2StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * R2 存储服务实现
 */
@Slf4j
@Service
public class R2StorageServiceImpl implements R2StorageService {

    @Autowired
    private S3Client s3Client;

    @Value("${r2.bucket-name}")
    private String bucketName;

    @Value("${r2.public-url}")
    private String publicUrl;

    private static final int THUMBNAIL_MAX_SIZE = 400; // 缩略图最大尺寸

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        try {
            String key = generateKey(folder, file.getOriginalFilename());

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            return publicUrl + "/" + key;
        } catch (Exception e) {
            log.error("Failed to upload file: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public String[] uploadImageWithThumbnail(MultipartFile file, String folder) {
        try {
            // 上传原图
            String originalKey = generateKey(folder, file.getOriginalFilename());

            PutObjectRequest originalRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(originalKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(originalRequest, RequestBody.fromBytes(file.getBytes()));

            String originalUrl = publicUrl + "/" + originalKey;

            // 生成并上传缩略图
            String thumbnailUrl = null;
            try {
                byte[] thumbnailBytes = createThumbnail(file);
                if (thumbnailBytes != null) {
                    String thumbnailKey = generateKey(folder + "/thumbnails", file.getOriginalFilename());

                    PutObjectRequest thumbnailRequest = PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(thumbnailKey)
                            .contentType(file.getContentType())
                            .build();

                    s3Client.putObject(thumbnailRequest, RequestBody.fromBytes(thumbnailBytes));
                    thumbnailUrl = publicUrl + "/" + thumbnailKey;
                }
            } catch (Exception e) {
                log.warn("Failed to create thumbnail: {}", e.getMessage());
                // 缩略图失败不影响原图上传
            }

            return new String[] { originalUrl, thumbnailUrl };
        } catch (Exception e) {
            log.error("Failed to upload image: {}", e.getMessage(), e);
            throw new RuntimeException("图片上传失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            if (key == null) {
                return false;
            }

            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete file: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String extractKeyFromUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(publicUrl)) {
            return null;
        }
        return fileUrl.substring(publicUrl.length() + 1);
    }

    @Override
    public String uploadBytes(byte[] data, String folder, String filename, String contentType) {
        try {
            String key = generateKeyWithFilename(folder, filename);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(data));

            return publicUrl + "/" + key;
        } catch (Exception e) {
            log.error("Failed to upload bytes: {}", e.getMessage(), e);
            throw new RuntimeException("字节数据上传失败: " + e.getMessage());
        }
    }

    /**
     * 生成带文件名的key（保留原文件名）
     */
    private String generateKeyWithFilename(String folder, String filename) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("%s/%s/%s", folder, date, filename);
    }

    /**
     * 生成文件key
     */
    private String generateKey(String folder, String originalFilename) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return String.format("%s/%s/%s%s", folder, date, uuid, extension);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }

    /**
     * 创建缩略图
     */
    private byte[] createThumbnail(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            return null;
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 计算缩略图尺寸
        int targetWidth, targetHeight;
        if (originalWidth > originalHeight) {
            targetWidth = Math.min(originalWidth, THUMBNAIL_MAX_SIZE);
            targetHeight = (int) ((double) originalHeight / originalWidth * targetWidth);
        } else {
            targetHeight = Math.min(originalHeight, THUMBNAIL_MAX_SIZE);
            targetWidth = (int) ((double) originalWidth / originalHeight * targetHeight);
        }

        // 创建缩略图
        BufferedImage thumbnail = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = thumbnail.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        // 转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = getImageFormat(file.getContentType());
        ImageIO.write(thumbnail, formatName, baos);

        return baos.toByteArray();
    }

    /**
     * 获取图片格式
     */
    private String getImageFormat(String contentType) {
        if (contentType == null) {
            return "jpg";
        }
        switch (contentType.toLowerCase()) {
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/webp":
                return "webp";
            default:
                return "jpg";
        }
    }
}
