package com.wic.edu.kg.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wic.edu.kg.entity.GalleryImage;
import com.wic.edu.kg.vo.GalleryImageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 图片库服务接口
 */
public interface GalleryService extends IService<GalleryImage> {

    // ========== 公开接口 ==========

    /**
     * 获取已审核通过的图片列表（支持搜索）
     * 
     * @param category      分类筛选
     * @param keyword       关键词（搜索标题/描述/地点）
     * @param page          页码
     * @param size          每页数量
     * @param currentUserId 当前用户ID（用于判断是否点赞）
     * @return 图片列表
     */
    Page<GalleryImageVO> getApprovedImages(String category, String keyword, int page, int size, Long currentUserId);

    /**
     * 获取精选图片（首页展示）
     * 
     * @param limit 数量限制
     * @return 精选图片列表
     */
    List<GalleryImageVO> getFeaturedImages(int limit);

    /**
     * 获取图片详情
     * 
     * @param id            图片ID
     * @param currentUserId 当前用户ID
     * @return 图片详情
     */
    GalleryImageVO getImageDetail(Long id, Long currentUserId);

    /**
     * 增加浏览次数
     * 
     * @param imageId 图片ID
     */
    void incrementViewCount(Long imageId);

    // ========== 用户接口 ==========

    /**
     * 上传图片
     * 
     * @param userId      用户ID
     * @param file        图片文件
     * @param title       标题
     * @param description 描述
     * @param location    地点
     * @param category    分类
     * @param tags        标签
     * @return 图片信息
     */
    GalleryImage uploadImage(Long userId, MultipartFile file, String title, String description,
            String location, String category, String tags);

    /**
     * 获取用户上传的图片
     * 
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页数量
     * @return 图片列表
     */
    Page<GalleryImageVO> getUserImages(Long userId, int page, int size);

    /**
     * 删除图片（用户自己的）
     * 
     * @param userId  用户ID
     * @param imageId 图片ID
     */
    void deleteUserImage(Long userId, Long imageId);

    /**
     * 点赞/取消点赞
     * 
     * @param userId  用户ID
     * @param imageId 图片ID
     * @return 点赞后的点赞数
     */
    int toggleLike(Long userId, Long imageId);

    // ========== 管理接口 ==========

    /**
     * 获取所有图片（管理员）
     * 
     * @param status 状态筛选
     * @param page   页码
     * @param size   每页数量
     * @return 图片列表
     */
    Page<GalleryImage> getAllImages(Integer status, int page, int size);

    /**
     * 审核图片
     * 
     * @param imageId      图片ID
     * @param approved     是否通过
     * @param rejectReason 拒绝原因
     * @param reviewerId   审核人ID
     */
    void reviewImage(Long imageId, boolean approved, String rejectReason, Long reviewerId);

    /**
     * 批量审核图片
     * 
     * @param imageIds     图片ID列表
     * @param approved     是否通过
     * @param rejectReason 拒绝原因
     * @param reviewerId   审核人ID
     * @return 成功审核的数量
     */
    int batchReview(List<Long> imageIds, boolean approved, String rejectReason, Long reviewerId);

    /**
     * 设置精选
     * 
     * @param imageId  图片ID
     * @param featured 是否精选
     */
    void setFeatured(Long imageId, boolean featured);

    /**
     * 删除图片（管理员）
     * 
     * @param imageId 图片ID
     */
    void deleteImage(Long imageId);

    /**
     * 批量删除图片
     * 
     * @param imageIds 图片ID列表
     * @return 成功删除的数量
     */
    int batchDelete(List<Long> imageIds);
}
