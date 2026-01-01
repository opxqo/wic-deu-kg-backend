package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 日志查询结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "日志查询结果")
public class LogQueryVO {

    @Schema(description = "日志总数")
    private Integer totalCount;

    @Schema(description = "返回的日志数")
    private Integer returnedCount;

    @Schema(description = "最新日志ID（用于增量查询）")
    private Long latestId;

    @Schema(description = "日志列表")
    private List<LogEntryVO> logs;
}
