package com.wic.edu.kg.controller;

import com.wic.edu.kg.annotation.RequireRole;
import com.wic.edu.kg.common.ApiResponse;
import com.wic.edu.kg.config.InMemoryLogAppender;
import com.wic.edu.kg.enums.UserRole;
import com.wic.edu.kg.vo.LogEntryVO;
import com.wic.edu.kg.vo.LogQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 日志管理控制器（仅组织者）
 *
 * RESTful 设计 - 日志查询接口：
 * - GET /api/admin/logs - 获取日志列表
 * - GET /api/admin/logs/poll - 增量获取日志（用于轮询）
 * - DELETE /api/admin/logs - 清空日志
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/logs")
@Tag(name = "日志管理", description = "应用运行日志查询接口（仅组织者可访问）")
// @RequireRole(UserRole.ORGANIZER) // TODO: 测试完成后恢复权限限制
public class LogController {

    @GetMapping("/test")
    @Operation(summary = "测试日志记录", description = "生成测试日志条目")
    public ResponseEntity<ApiResponse<String>> testLog() {
        log.debug("这是一条 DEBUG 日志");
        log.info("这是一条 INFO 日志");
        log.warn("这是一条 WARN 日志");
        log.error("这是一条 ERROR 日志");
        return ResponseEntity.ok(ApiResponse.ok("已生成4条测试日志"));
    }

    @GetMapping
    @Operation(summary = "获取日志列表", description = "获取应用运行日志，支持按级别过滤和数量限制")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ResponseEntity<ApiResponse<LogQueryVO>> getLogs(
            @Parameter(description = "日志级别过滤 (DEBUG/INFO/WARN/ERROR)") @RequestParam(required = false) String level,
            @Parameter(description = "返回最近N条日志，默认500") @RequestParam(required = false, defaultValue = "500") Integer limit) {

        List<LogEntryVO> logs;

        if (level != null && !level.isEmpty()) {
            logs = InMemoryLogAppender.getLogsByLevel(level);
        } else {
            logs = InMemoryLogAppender.getLogs();
        }

        // 限制返回数量
        if (logs.size() > limit) {
            logs = logs.subList(logs.size() - limit, logs.size());
        }

        Long latestId = logs.isEmpty() ? 0L : logs.get(logs.size() - 1).getId();

        LogQueryVO result = LogQueryVO.builder()
                .totalCount(InMemoryLogAppender.getLogCount())
                .returnedCount(logs.size())
                .latestId(latestId)
                .logs(logs)
                .build();

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/poll")
    @Operation(summary = "增量获取日志", description = "获取指定ID之后的新日志，用于前端轮询")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ResponseEntity<ApiResponse<LogQueryVO>> pollLogs(
            @Parameter(description = "上次获取的最新日志ID，返回此ID之后的日志") @RequestParam(required = false, defaultValue = "0") Long afterId,
            @Parameter(description = "日志级别过滤 (DEBUG/INFO/WARN/ERROR)") @RequestParam(required = false) String level) {

        List<LogEntryVO> logs = InMemoryLogAppender.getLogsAfterId(afterId);

        // 按级别过滤
        if (level != null && !level.isEmpty()) {
            logs = logs.stream()
                    .filter(log -> log.getLevel().equalsIgnoreCase(level))
                    .toList();
        }

        Long latestId = logs.isEmpty() ? afterId : logs.get(logs.size() - 1).getId();

        LogQueryVO result = LogQueryVO.builder()
                .totalCount(InMemoryLogAppender.getLogCount())
                .returnedCount(logs.size())
                .latestId(latestId)
                .logs(logs)
                .build();

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @DeleteMapping
    @Operation(summary = "清空日志", description = "清空内存中的所有日志")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "清空成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ResponseEntity<ApiResponse<Void>> clearLogs() {
        InMemoryLogAppender.clearLogs();
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
