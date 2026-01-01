package com.wic.edu.kg.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import com.wic.edu.kg.vo.LogEntryVO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存日志Appender
 * 将日志存储在内存中供API查询
 */
public class InMemoryLogAppender extends AppenderBase<ILoggingEvent> {

    private static final int MAX_LOG_SIZE = 2000; // 最多保存2000条日志
    private static final ConcurrentLinkedDeque<LogEntryVO> logs = new ConcurrentLinkedDeque<>();
    private static final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    protected void append(ILoggingEvent event) {
        LogEntryVO entry = LogEntryVO.builder()
                .id(idGenerator.incrementAndGet())
                .timestamp(LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(event.getTimeStamp()),
                        ZoneId.systemDefault()))
                .level(event.getLevel().toString())
                .threadName(event.getThreadName())
                .loggerName(event.getLoggerName())
                .message(event.getFormattedMessage())
                .stackTrace(getStackTrace(event))
                .build();

        logs.addLast(entry);

        // 保持日志数量在限制内
        while (logs.size() > MAX_LOG_SIZE) {
            logs.pollFirst();
        }
    }

    private String getStackTrace(ILoggingEvent event) {
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (throwableProxy == null) {
            return null;
        }
        return ThrowableProxyUtil.asString(throwableProxy);
    }

    /**
     * 获取所有日志
     */
    public static List<LogEntryVO> getLogs() {
        return new ArrayList<>(logs);
    }

    /**
     * 获取指定级别的日志
     */
    public static List<LogEntryVO> getLogsByLevel(String level) {
        if (level == null || level.isEmpty()) {
            return getLogs();
        }
        return logs.stream()
                .filter(log -> log.getLevel().equalsIgnoreCase(level))
                .toList();
    }

    /**
     * 获取最近N条日志
     */
    public static List<LogEntryVO> getRecentLogs(int count) {
        List<LogEntryVO> allLogs = getLogs();
        int size = allLogs.size();
        if (count >= size) {
            return allLogs;
        }
        return allLogs.subList(size - count, size);
    }

    /**
     * 获取指定ID之后的日志（用于增量获取）
     */
    public static List<LogEntryVO> getLogsAfterId(long afterId) {
        return logs.stream()
                .filter(log -> log.getId() > afterId)
                .toList();
    }

    /**
     * 清空日志
     */
    public static void clearLogs() {
        logs.clear();
    }

    /**
     * 获取日志总数
     */
    public static int getLogCount() {
        return logs.size();
    }
}
