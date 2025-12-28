package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wic.edu.kg.common.ApiResponse;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 留言资源控制器
 * 
 * RESTful 设计：
 * - GET /api/messages - 获取留言列表
 * - POST /api/messages - 发布留言
 * - DELETE /api/messages/{id} - 删除留言
 * - POST /api/messages/{id}/likes - 点赞
 * - GET /api/messages/fonts - 获取字体列表
 * - GET /api/users/me/messages - 获取我的留言
 */
@RestController
@Tag(name = "留言资源", description = "学长学姐留言板")
public class SeniorMessageController {

    @Autowired
    private SeniorMessageService messageService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtUtil jwtUtil;

    // ==================== 公开接口 ====================

    @GetMapping("/api/messages")
    @Operation(summary = "获取留言列表", description = "获取已发布的留言列表，支持分页和关键词搜索")
    public ResponseEntity<ApiResponse<Page<SeniorMessageVO>>> getMessages(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = extractUserId(authorization);
        Page<SeniorMessageVO> result = messageService.getMessages(page, size, keyword, userId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/api/messages/fonts")
    @Operation(summary = "获取可用字体列表", description = "获取所有可用的字体选项")
    public ResponseEntity<ApiResponse<List<MessageFont>>> getFonts() {
        return ResponseEntity.ok(ApiResponse.ok(messageService.getAvailableFonts()));
    }

    // ==================== 需认证接口 ====================

    @PostMapping("/api/messages")
    @Operation(summary = "发布留言", description = "发布一条新留言")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "发布成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<ApiResponse<SeniorMessage>> createMessage(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateMessageRequest request) {
        Long userId = getUserId(authorization);
        SeniorMessage message = messageService.createMessage(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(message));
    }

    @DeleteMapping("/api/messages/{id}")
    @Operation(summary = "删除留言", description = "删除自己发布的留言")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权删除")
    })
    public ResponseEntity<Void> deleteMessage(
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "留言ID", required = true) @PathVariable Long id) {
        Long userId = getUserId(authorization);
        messageService.deleteMessage(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/messages/{id}/likes")
    @Operation(summary = "点赞留言", description = "切换点赞状态")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "点赞成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<ApiResponse<Integer>> toggleLike(
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "留言ID", required = true) @PathVariable Long id) {
        Long userId = getUserId(authorization);
        int likeCount = messageService.toggleLike(userId, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(likeCount));
    }

    @GetMapping("/api/users/me/messages")
    @Operation(summary = "获取我的留言", description = "获取当前用户发布的留言列表")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<ApiResponse<Page<SeniorMessageVO>>> getMyMessages(
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        Long userId = getUserId(authorization);
        Page<SeniorMessageVO> result = messageService.getUserMessages(userId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // ==================== 辅助方法 ====================

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
