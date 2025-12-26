package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.dto.CreateMessageRequest;
import com.wic.edu.kg.entity.MessageFont;
import com.wic.edu.kg.entity.SeniorMessage;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.service.SeniorMessageService;
import com.wic.edu.kg.service.SysUserService;
import com.wic.edu.kg.utils.JwtUtil;
import com.wic.edu.kg.vo.SeniorMessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学长学姐留言板控制器
 */
@RestController
@Tag(name = "学长学姐留言板", description = "留言发布、查看、点赞")
public class SeniorMessageController {

    @Autowired
    private SeniorMessageService messageService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtUtil jwtUtil;

    // ========== 公开接口 ==========

    @GetMapping("/api/public/messages")
    @Operation(summary = "获取留言列表", description = "获取已发布的留言列表，支持分页和关键词搜索")
    public Result<Page<SeniorMessageVO>> getMessages(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = extractUserId(authorization);
        Page<SeniorMessageVO> result = messageService.getMessages(page, size, keyword, userId);
        return Result.success(result);
    }

    @GetMapping("/api/public/messages/fonts")
    @Operation(summary = "获取可用字体列表", description = "获取所有可用的字体选项")
    public Result<List<MessageFont>> getFonts() {
        return Result.success(messageService.getAvailableFonts());
    }

    @GetMapping("/api/public/messages/search")
    @Operation(summary = "搜索留言", description = "根据关键词搜索留言内容或署名")
    public Result<Page<SeniorMessageVO>> searchMessages(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = extractUserId(authorization);
        Page<SeniorMessageVO> result = messageService.getMessages(page, size, keyword, userId);
        return Result.success(result);
    }

    // ========== 用户接口（需登录） ==========

    @PostMapping("/api/messages")
    @Operation(summary = "发布留言", description = "发布一条新留言")
    @ApiResponse(responseCode = "200", description = "发布成功")
    public Result<SeniorMessage> createMessage(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateMessageRequest request) {
        Long userId = getUserId(authorization);
        SeniorMessage message = messageService.createMessage(userId, request);
        return Result.success(message);
    }

    @DeleteMapping("/api/messages/{id}")
    @Operation(summary = "删除留言", description = "删除自己发布的留言")
    public Result<Void> deleteMessage(
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "留言ID") @PathVariable Long id) {
        Long userId = getUserId(authorization);
        messageService.deleteMessage(userId, id);
        return Result.success();
    }

    @PostMapping("/api/messages/{id}/like")
    @Operation(summary = "点赞/取消点赞", description = "切换点赞状态")
    public Result<Integer> toggleLike(
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "留言ID") @PathVariable Long id) {
        Long userId = getUserId(authorization);
        int likeCount = messageService.toggleLike(userId, id);
        return Result.success(likeCount);
    }

    @GetMapping("/api/messages/my")
    @Operation(summary = "获取我的留言", description = "获取当前用户发布的留言列表")
    public Result<Page<SeniorMessageVO>> getMyMessages(
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        Long userId = getUserId(authorization);
        Page<SeniorMessageVO> result = messageService.getUserMessages(userId, page, size);
        return Result.success(result);
    }

    // ========== 辅助方法 ==========

    private Long getUserId(String authorization) {
        String token = authorization.replace("Bearer ", "");
        String studentId = jwtUtil.extractUsername(token);
        SysUser user = sysUserService.getByStudentId(studentId);
        return user != null ? user.getId() : null;
    }

    private Long extractUserId(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        try {
            return getUserId(authorization);
        } catch (Exception e) {
            return null;
        }
    }
}
