package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 日志条目VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "日志条目")
public class LogEntryVO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "时间戳")
    private LocalDateTime timestamp;

    @Schema(description = "日志级别", example = "INFO")
    private String level;

    @Schema(description = "线程名")
    private String threadName;

    @Schema(description = "Logger名称")
    private String loggerName;

    @Schema(description = "日志消息")
    private String message;

    @Schema(description = "异常堆栈（如果有）")
    private String stackTrace;
}
