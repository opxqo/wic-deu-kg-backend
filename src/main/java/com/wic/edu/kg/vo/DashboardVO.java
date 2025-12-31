package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 仪表盘数据视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "仪表盘数据")
public class DashboardVO {

    @Schema(description = "统计卡片数据")
    private StatCard statCard;

    @Schema(description = "待处理项")
    private PendingItems pendingItems;

    @Schema(description = "趋势数据")
    private TrendData trendData;

    @Schema(description = "系统状态")
    private SystemStatus systemStatus;

    // ==================== 嵌套类型定义 ====================

    /**
     * 统计卡片 - 核心指标
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "统计卡片")
    public static class StatCard {

        @Schema(description = "用户总数")
        private Long totalUsers;

        @Schema(description = "今日新增用户")
        private Long newUsersToday;

        @Schema(description = "文章总数")
        private Long totalArticles;

        @Schema(description = "已发布文章数")
        private Long publishedArticles;

        @Schema(description = "图片总数")
        private Long totalGalleryImages;

        @Schema(description = "留言总数")
        private Long totalMessages;
    }

    /**
     * 待处理项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "待处理项")
    public static class PendingItems {

        @Schema(description = "待审核留言数")
        private Long pendingMessages;

        @Schema(description = "待审核图片数")
        private Long pendingImages;

        @Schema(description = "禁用用户数")
        private Long disabledUsers;

        @Schema(description = "草稿文章数")
        private Long draftArticles;
    }

    /**
     * 趋势数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "趋势数据")
    public static class TrendData {

        @Schema(description = "最近7天用户增长趋势")
        private List<DailyCount> userTrend;

        @Schema(description = "最近7天文章增长趋势")
        private List<DailyCount> articleTrend;
    }

    /**
     * 每日统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "每日统计")
    public static class DailyCount {

        @Schema(description = "日期 (yyyy-MM-dd)")
        private String date;

        @Schema(description = "数量")
        private Long count;
    }

    /**
     * 系统状态
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "系统状态")
    public static class SystemStatus {

        @Schema(description = "服务器时间")
        private LocalDateTime serverTime;

        @Schema(description = "JVM已用内存(MB)")
        private Long jvmMemoryUsed;

        @Schema(description = "JVM最大内存(MB)")
        private Long jvmMemoryMax;

        @Schema(description = "CPU核心数")
        private Integer cpuCores;

        @Schema(description = "操作系统")
        private String osName;

        @Schema(description = "Java版本")
        private String javaVersion;
    }
}
