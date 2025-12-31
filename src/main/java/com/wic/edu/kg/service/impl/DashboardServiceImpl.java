package com.wic.edu.kg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wic.edu.kg.entity.Article;
import com.wic.edu.kg.entity.GalleryImage;
import com.wic.edu.kg.entity.SeniorMessage;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.mapper.ArticleMapper;
import com.wic.edu.kg.mapper.GalleryImageMapper;
import com.wic.edu.kg.mapper.SeniorMessageMapper;
import com.wic.edu.kg.mapper.SysUserMapper;
import com.wic.edu.kg.service.DashboardService;
import com.wic.edu.kg.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 仪表盘服务实现
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SysUserMapper sysUserMapper;
    private final ArticleMapper articleMapper;
    private final GalleryImageMapper galleryImageMapper;
    private final SeniorMessageMapper seniorMessageMapper;

    @Override
    public DashboardVO getDashboardData() {
        return DashboardVO.builder()
                .statCard(buildStatCard())
                .pendingItems(buildPendingItems())
                .trendData(buildTrendData())
                .systemStatus(buildSystemStatus())
                .build();
    }

    /**
     * 构建统计卡片数据
     */
    private DashboardVO.StatCard buildStatCard() {
        // 用户统计
        Long totalUsers = sysUserMapper.selectCount(null);

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        Long newUsersToday = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .ge(SysUser::getCreatedAt, todayStart));

        // 文章统计
        Long totalArticles = articleMapper.selectCount(null);
        Long publishedArticles = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1) // 已发布
        );

        // 图片统计
        Long totalGalleryImages = galleryImageMapper.selectCount(null);

        // 留言统计
        Long totalMessages = seniorMessageMapper.selectCount(null);

        return DashboardVO.StatCard.builder()
                .totalUsers(totalUsers)
                .newUsersToday(newUsersToday)
                .totalArticles(totalArticles)
                .publishedArticles(publishedArticles)
                .totalGalleryImages(totalGalleryImages)
                .totalMessages(totalMessages)
                .build();
    }

    /**
     * 构建待处理项数据
     */
    private DashboardVO.PendingItems buildPendingItems() {
        // 待审核留言 (status=0)
        Long pendingMessages = seniorMessageMapper.selectCount(
                new LambdaQueryWrapper<SeniorMessage>()
                        .eq(SeniorMessage::getStatus, 0));

        // 待审核图片 (status=0)
        Long pendingImages = galleryImageMapper.selectCount(
                new LambdaQueryWrapper<GalleryImage>()
                        .eq(GalleryImage::getStatus, 0));

        // 禁用用户 (status=2)
        Long disabledUsers = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getStatus, 2));

        // 草稿文章 (status=0)
        Long draftArticles = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 0));

        return DashboardVO.PendingItems.builder()
                .pendingMessages(pendingMessages)
                .pendingImages(pendingImages)
                .disabledUsers(disabledUsers)
                .draftArticles(draftArticles)
                .build();
    }

    /**
     * 构建趋势数据 - 最近7天
     */
    private DashboardVO.TrendData buildTrendData() {
        List<DashboardVO.DailyCount> userTrend = new ArrayList<>();
        List<DashboardVO.DailyCount> articleTrend = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);
            String dateStr = date.format(formatter);

            // 当日新增用户数
            Long userCount = sysUserMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>()
                            .ge(SysUser::getCreatedAt, dayStart)
                            .le(SysUser::getCreatedAt, dayEnd));
            userTrend.add(DashboardVO.DailyCount.builder()
                    .date(dateStr)
                    .count(userCount)
                    .build());

            // 当日新增文章数
            Long articleCount = articleMapper.selectCount(
                    new LambdaQueryWrapper<Article>()
                            .ge(Article::getCreatedAt, dayStart)
                            .le(Article::getCreatedAt, dayEnd));
            articleTrend.add(DashboardVO.DailyCount.builder()
                    .date(dateStr)
                    .count(articleCount)
                    .build());
        }

        return DashboardVO.TrendData.builder()
                .userTrend(userTrend)
                .articleTrend(articleTrend)
                .build();
    }

    /**
     * 构建系统状态
     */
    private DashboardVO.SystemStatus buildSystemStatus() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long maxMemory = runtime.maxMemory() / (1024 * 1024);

        return DashboardVO.SystemStatus.builder()
                .serverTime(LocalDateTime.now())
                .jvmMemoryUsed(usedMemory)
                .jvmMemoryMax(maxMemory)
                .cpuCores(runtime.availableProcessors())
                .osName(System.getProperty("os.name"))
                .javaVersion(System.getProperty("java.version"))
                .build();
    }
}
