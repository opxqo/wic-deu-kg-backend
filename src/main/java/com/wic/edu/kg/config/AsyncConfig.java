package com.wic.edu.kg.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 异步配置类
 * 启用 @Async 注解支持，用于邮件发送等异步操作
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
