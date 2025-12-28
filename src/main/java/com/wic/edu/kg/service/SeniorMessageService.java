package com.wic.edu.kg.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wic.edu.kg.dto.CreateMessageRequest;
import com.wic.edu.kg.entity.MessageFont;
import com.wic.edu.kg.entity.SeniorMessage;
import com.wic.edu.kg.vo.SeniorMessageVO;

import java.util.List;

/**
 * 学长学姐留言服务
 */
public interface SeniorMessageService extends IService<SeniorMessage> {

    /**
     * 获取留言列表（支持关键词搜索）
     */
    Page<SeniorMessageVO> getMessages(int page, int size, String keyword, Long currentUserId);

    /**
     * 发布留言
     */
    SeniorMessage createMessage(Long userId, CreateMessageRequest request);

    /**
     * 删除留言
     */
    void deleteMessage(Long userId, Long messageId);

    /**
     * 点赞/取消点赞
     */
    int toggleLike(Long userId, Long messageId);

    /**
     * 获取用户自己的留言列表
     */
    Page<SeniorMessageVO> getUserMessages(Long userId, int page, int size);

    /**
     * 根据用户ID获取留言列表（公开查看，只返回已发布的）
     */
    Page<SeniorMessageVO> getMessagesByUserId(Long userId, int page, int size, Long currentUserId);

    /**
     * 获取可用字体列表
     */
    List<MessageFont> getAvailableFonts();

    // ==================== 管理员接口 ====================

    /**
     * 管理员获取留言列表（支持状态筛选和关键词搜索）
     */
    Page<SeniorMessageVO> adminGetMessages(Integer status, String keyword, int page, int size);

    /**
     * 按状态统计留言数量
     */
    long countByStatus(Integer status);

    /**
     * 审核留言
     */
    void reviewMessage(Long messageId, Integer status, String reason);

    /**
     * 修改留言状态
     */
    void updateStatus(Long messageId, Integer status);

    /**
     * 管理员删除留言
     */
    void adminDeleteMessage(Long messageId);

    /**
     * 批量审核留言
     */
    int batchReview(List<Long> messageIds, Integer status, String reason);

    /**
     * 批量删除留言
     */
    int batchDelete(List<Long> messageIds);
}
