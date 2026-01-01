package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据库备份信息VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "数据库备份信息")
public class DatabaseBackupVO {

    @Schema(description = "数据库名称", example = "wic_edu_kg")
    private String databaseName;

    @Schema(description = "数据库版本", example = "8.0.33")
    private String databaseVersion;

    @Schema(description = "数据库大小（字节）")
    private Long databaseSizeBytes;

    @Schema(description = "数据库大小（可读格式）", example = "128.5 MB")
    private String databaseSizeFormatted;

    @Schema(description = "表数量")
    private Integer tableCount;

    @Schema(description = "总记录数")
    private Long totalRecords;

    @Schema(description = "备份文件URL（上传到R2后返回）")
    private String backupFileUrl;

    @Schema(description = "备份生成时间")
    private LocalDateTime backupTime;

    @Schema(description = "表详细信息")
    private List<TableInfo> tables;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "表信息")
    public static class TableInfo {

        @Schema(description = "表名")
        private String tableName;

        @Schema(description = "表注释")
        private String tableComment;

        @Schema(description = "记录数")
        private Long rowCount;

        @Schema(description = "数据大小（字节）")
        private Long dataSizeBytes;

        @Schema(description = "数据大小（可读格式）")
        private String dataSizeFormatted;

        @Schema(description = "索引大小（字节）")
        private Long indexSizeBytes;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;

        @Schema(description = "最后更新时间")
        private LocalDateTime updateTime;
    }
}
