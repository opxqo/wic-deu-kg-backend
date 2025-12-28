package com.wic.edu.kg.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置
 * 
 * 使用 Caffeine 作为本地缓存实现，提供高性能的缓存功能
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 缓存名称常量
     */
    public static final String DEPARTMENTS_CACHE = "departments";
    public static final String DEPARTMENT_BY_ID = "departmentById";
    public static final String DEPARTMENT_BY_CODE = "departmentByCode";
    public static final String IMAGE_CATEGORIES = "imageCategories";
    public static final String FEATURED_IMAGES = "featuredImages";
    public static final String USER_CARD = "userCard";
    public static final String MESSAGE_FONTS = "messageFonts";

    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // 默认缓存配置：10分钟过期，最大1000条
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .recordStats());

        // 注册所有缓存名称
        cacheManager.setCacheNames(java.util.List.of(
                DEPARTMENTS_CACHE,
                DEPARTMENT_BY_ID,
                DEPARTMENT_BY_CODE,
                IMAGE_CATEGORIES,
                FEATURED_IMAGES,
                USER_CARD,
                MESSAGE_FONTS));

        return cacheManager;
    }

    /**
     * 短期缓存管理器（1分钟过期，用于热点数据）
     */
    @Bean
    public CacheManager shortTermCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(500)
                .recordStats());
        return cacheManager;
    }
}
