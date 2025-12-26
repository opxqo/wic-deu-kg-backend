package com.wic.edu.kg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wic.edu.kg.dto.CreateMessageRequest;
import com.wic.edu.kg.entity.MessageFont;
import com.wic.edu.kg.entity.SeniorMessage;
import com.wic.edu.kg.entity.SeniorMessageLike;
import com.wic.edu.kg.exception.BusinessException;
import com.wic.edu.kg.mapper.MessageFontMapper;
import com.wic.edu.kg.mapper.SeniorMessageLikeMapper;
import com.wic.edu.kg.mapper.SeniorMessageMapper;
import com.wic.edu.kg.service.SeniorMessageService;
import com.wic.edu.kg.vo.SeniorMessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学长学姐留言服务实现
 */
@Service
public class SeniorMessageServiceImpl extends ServiceImpl<SeniorMessageMapper, SeniorMessage>
        implements SeniorMessageService {

    @Autowired
    private SeniorMessageLikeMapper likeMapper;

    @Autowired
    private MessageFontMapper fontMapper;

    @Override
    public Page<SeniorMessageVO> getMessages(int page, int size, String keyword, Long currentUserId) {
        // 1. 查询已发布的留言
        Page<SeniorMessage> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SeniorMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeniorMessage::getStatus, 1);

        // 关键词搜索（内容或署名）
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                    .like(SeniorMessage::getContent, keyword)
                    .or()
                    .like(SeniorMessage::getSignature, keyword));
        }

        wrapper.orderByDesc(SeniorMessage::getCreatedAt);
        Page<SeniorMessage> result = this.page(pageParam, wrapper);

        if (result.getRecords().isEmpty()) {
            return new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        }

        List<Long> messageIds = result.getRecords().stream()
                .map(SeniorMessage::getId).collect(Collectors.toList());
        List<Long> fontIds = result.getRecords().stream()
                .map(SeniorMessage::getFontId).distinct().collect(Collectors.toList());

        // 2. 批量查询点赞状态
        Set<Long> likedIds = batchGetLikedIds(currentUserId, messageIds);

        // 3. 批量查询字体信息
        Map<Long, MessageFont> fontMap = batchGetFonts(fontIds);

        // 4. 转换为VO
        Page<SeniorMessageVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(msg -> convertToVO(msg, likedIds.contains(msg.getId()), fontMap))
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    @Transactional
    public SeniorMessage createMessage(Long userId, CreateMessageRequest request) {
        SeniorMessage message = new SeniorMessage();
        message.setUserId(userId);
        message.setContent(request.getContent());
        message.setSignature(request.getSignature());
        message.setCardColor(request.getCardColor());
        message.setInkColor(request.getInkColor());
        message.setFontId(request.getFontId());
        message.setStatus(1); // 默认已发布
        message.setLikeCount(0);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        this.save(message);
        return message;
    }

    @Override
    @Transactional
    public void deleteMessage(Long userId, Long messageId) {
        SeniorMessage message = this.getById(messageId);
        if (message == null) {
            throw new BusinessException(404, "留言不存在");
        }
        if (!message.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权删除此留言");
        }
        // 删除点赞记录
        likeMapper.delete(new LambdaQueryWrapper<SeniorMessageLike>()
                .eq(SeniorMessageLike::getMessageId, messageId));
        // 删除留言
        this.removeById(messageId);
    }

    @Override
    @Transactional
    public int toggleLike(Long userId, Long messageId) {
        SeniorMessage message = this.getById(messageId);
        if (message == null || message.getStatus() != 1) {
            throw new BusinessException(404, "留言不存在");
        }

        LambdaQueryWrapper<SeniorMessageLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeniorMessageLike::getMessageId, messageId)
                .eq(SeniorMessageLike::getUserId, userId);
        SeniorMessageLike existingLike = likeMapper.selectOne(wrapper);

        if (existingLike != null) {
            // 取消点赞
            likeMapper.deleteById(existingLike.getId());
            this.update(new LambdaUpdateWrapper<SeniorMessage>()
                    .eq(SeniorMessage::getId, messageId)
                    .setSql("like_count = like_count - 1"));
            return message.getLikeCount() - 1;
        } else {
            // 点赞
            SeniorMessageLike like = new SeniorMessageLike();
            like.setMessageId(messageId);
            like.setUserId(userId);
            like.setCreatedAt(LocalDateTime.now());
            likeMapper.insert(like);
            this.update(new LambdaUpdateWrapper<SeniorMessage>()
                    .eq(SeniorMessage::getId, messageId)
                    .setSql("like_count = like_count + 1"));
            return message.getLikeCount() + 1;
        }
    }

    @Override
    public Page<SeniorMessageVO> getUserMessages(Long userId, int page, int size) {
        Page<SeniorMessage> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SeniorMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeniorMessage::getUserId, userId)
                .orderByDesc(SeniorMessage::getCreatedAt);
        Page<SeniorMessage> result = this.page(pageParam, wrapper);

        if (result.getRecords().isEmpty()) {
            return new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        }

        List<Long> messageIds = result.getRecords().stream()
                .map(SeniorMessage::getId).collect(Collectors.toList());
        List<Long> fontIds = result.getRecords().stream()
                .map(SeniorMessage::getFontId).distinct().collect(Collectors.toList());

        Set<Long> likedIds = batchGetLikedIds(userId, messageIds);
        Map<Long, MessageFont> fontMap = batchGetFonts(fontIds);

        Page<SeniorMessageVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(msg -> convertToVO(msg, likedIds.contains(msg.getId()), fontMap))
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public List<MessageFont> getAvailableFonts() {
        return fontMapper.selectList(new LambdaQueryWrapper<MessageFont>()
                .eq(MessageFont::getStatus, 1)
                .orderByAsc(MessageFont::getSortOrder));
    }

    // ========== 辅助方法 ==========

    private Set<Long> batchGetLikedIds(Long userId, List<Long> messageIds) {
        if (userId == null || messageIds.isEmpty()) {
            return Set.of();
        }
        List<SeniorMessageLike> likes = likeMapper.selectList(new LambdaQueryWrapper<SeniorMessageLike>()
                .eq(SeniorMessageLike::getUserId, userId)
                .in(SeniorMessageLike::getMessageId, messageIds));
        return likes.stream().map(SeniorMessageLike::getMessageId).collect(Collectors.toSet());
    }

    private Map<Long, MessageFont> batchGetFonts(List<Long> fontIds) {
        if (fontIds.isEmpty()) {
            return Map.of();
        }
        List<MessageFont> fonts = fontMapper.selectBatchIds(fontIds);
        return fonts.stream().collect(Collectors.toMap(MessageFont::getId, f -> f));
    }

    private SeniorMessageVO convertToVO(SeniorMessage message, boolean liked, Map<Long, MessageFont> fontMap) {
        SeniorMessageVO vo = new SeniorMessageVO();
        vo.setId(message.getId());
        vo.setContent(message.getContent());
        vo.setSignature(message.getSignature());
        vo.setCardColor(message.getCardColor());
        vo.setInkColor(message.getInkColor());
        vo.setLikeCount(message.getLikeCount());
        vo.setLiked(liked);
        vo.setCreatedAt(message.getCreatedAt());

        // 字体信息
        MessageFont font = fontMap.get(message.getFontId());
        if (font != null) {
            SeniorMessageVO.FontInfo fontInfo = new SeniorMessageVO.FontInfo();
            fontInfo.setId(font.getId());
            fontInfo.setName(font.getName());
            fontInfo.setCssClass(font.getCssClass());
            fontInfo.setFontFamily(font.getFontFamily());
            vo.setFont(fontInfo);
        }

        return vo;
    }
}
