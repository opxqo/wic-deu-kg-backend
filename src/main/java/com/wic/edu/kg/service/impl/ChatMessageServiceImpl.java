package com.wic.edu.kg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wic.edu.kg.entity.ChatMessage;
import com.wic.edu.kg.mapper.ChatMessageMapper;
import com.wic.edu.kg.service.ChatMessageService;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {
}
