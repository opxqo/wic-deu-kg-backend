package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.entity.ChatMessage;
import com.wic.edu.kg.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "聊天管理", description = "聊天消息相关接口")
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

    // REST endpoint to get chat history
    @GetMapping("/history/{departmentId}")
    @Operation(summary = "获取聊天历史", description = "根据部门ID获取历史消息")
    public Result<List<ChatMessage>> getChatHistory(@PathVariable Long departmentId) {
        List<ChatMessage> history = chatMessageService.list(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getDepartmentId, departmentId)
                        .orderByAsc(ChatMessage::getCreatedAt)
        );
        return Result.success(history);
    }
}
