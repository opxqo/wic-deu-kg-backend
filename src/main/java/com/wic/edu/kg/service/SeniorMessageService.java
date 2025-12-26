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
     * 获取可用字体列表
     */
    List<MessageFont> getAvailableFonts();
}
