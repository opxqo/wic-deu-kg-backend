package com.wic.edu.kg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wic.edu.kg.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
