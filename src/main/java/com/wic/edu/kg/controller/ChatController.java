package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wic.edu.kg.common.ApiResponse;
import com.wic.edu.kg.entity.ChatMessage;
import com.wic.edu.kg.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天消息控制器
 * 
 * RESTful 设计：
 * - GET /api/departments/{departmentId}/messages - 获取学部聊天记录
 * 
 * WebSocket:
 * - /app/sendMessage -> /topic/public - 发送消息
 */
@RestController
@Tag(name = "聊天消息", description = "聊天消息相关接口")
public class ChatController {

    @Autowired
    private ChatMessageService chatMessageService;

    // WebSocket endpoint to handle sending messages
    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setCreatedAt(LocalDateTime.now());
        chatMessageService.save(chatMessage);
        return chatMessage;
    }

    // REST endpoint to get chat history - 符合 RESTful 规范
    @GetMapping("/api/departments/{departmentId}/messages")
    @Operation(summary = "获取学部聊天记录", description = "获取指定学部的历史聊天消息")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
    })
    public ResponseEntity<ApiResponse<List<ChatMessage>>> getChatHistory(
            @Parameter(description = "学部ID", required = true) @PathVariable Long departmentId) {
        List<ChatMessage> history = chatMessageService.list(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getDepartmentId, departmentId)
                        .orderByAsc(ChatMessage::getCreatedAt));
        return ResponseEntity.ok(ApiResponse.ok(history));
    }
}
