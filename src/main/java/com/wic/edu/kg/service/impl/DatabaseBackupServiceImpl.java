package com.wic.edu.kg.service.impl;

import com.wic.edu.kg.service.DatabaseBackupService;
import com.wic.edu.kg.service.R2StorageService;
import com.wic.edu.kg.vo.DatabaseBackupVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据库备份服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseBackupServiceImpl implements DatabaseBackupService {

    private final JdbcTemplate jdbcTemplate;
    private final R2StorageService r2StorageService;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Override
    public DatabaseBackupVO getDatabaseBackupInfo() {
        String databaseName = extractDatabaseName(datasourceUrl);
        log.info("正在获取数据库信息: {}", databaseName);

        String databaseVersion = getDatabaseVersion();
        List<DatabaseBackupVO.TableInfo> tables = getTableInfoList(databaseName);

        log.info("获取到 {} 张表", tables.size());

        long totalSize = tables.stream().mapToLong(t -> t.getDataSizeBytes() + t.getIndexSizeBytes()).sum();
        long totalRecords = tables.stream().mapToLong(DatabaseBackupVO.TableInfo::getRowCount).sum();

        return DatabaseBackupVO.builder()
                .databaseName(databaseName)
                .databaseVersion(databaseVersion)
                .databaseSizeBytes(totalSize)
                .databaseSizeFormatted(formatBytes(totalSize))
                .tableCount(tables.size())
                .totalRecords(totalRecords)
                .backupTime(LocalDateTime.now())
                .tables(tables)
                .build();
    }

    @Override
    public DatabaseBackupVO backupAndUploadToR2() {
        String databaseName = extractDatabaseName(datasourceUrl);
        log.info("开始备份数据库: {}", databaseName);

        String databaseVersion = getDatabaseVersion();
        List<DatabaseBackupVO.TableInfo> tables = getTableInfoList(databaseName);

        log.info("获取到 {} 张表进行备份", tables.size());

        long totalSize = tables.stream().mapToLong(t -> t.getDataSizeBytes() + t.getIndexSizeBytes()).sum();
        long totalRecords = tables.stream().mapToLong(DatabaseBackupVO.TableInfo::getRowCount).sum();
        LocalDateTime backupTime = LocalDateTime.now();

        // 生成SQL备份内容
        String sqlContent = generateSqlBackup(databaseName, tables);
        log.info("生成SQL备份内容，长度: {} 字符", sqlContent.length());

        // 生成备份文件名
        String filename = String.format("%s_backup_%s.sql",
                databaseName,
                backupTime.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

        // 上传到R2
        String backupFileUrl = r2StorageService.uploadBytes(
                sqlContent.getBytes(StandardCharsets.UTF_8),
                "db",
                filename,
                "application/sql");

        log.info("数据库备份已上传到R2: {}", backupFileUrl);

        return DatabaseBackupVO.builder()
                .databaseName(databaseName)
                .databaseVersion(databaseVersion)
                .databaseSizeBytes(totalSize)
                .databaseSizeFormatted(formatBytes(totalSize))
                .tableCount(tables.size())
                .totalRecords(totalRecords)
                .backupFileUrl(backupFileUrl)
                .backupTime(backupTime)
                .tables(tables)
                .build();
    }

    /**
     * 生成SQL备份内容
     */
    private String generateSqlBackup(String databaseName, List<DatabaseBackupVO.TableInfo> tables) {
        StringBuilder sql = new StringBuilder();

        // 备份头部注释
        sql.append("-- ================================================================\n");
        sql.append("-- WIC EDU KG Database Backup\n");
        sql.append("-- Database: ").append(databaseName).append("\n");
        sql.append("-- Generated: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sql.append("-- Tables: ").append(tables.size()).append("\n");
        sql.append("-- ================================================================\n\n");

        sql.append("SET NAMES utf8mb4;\n");
        sql.append("SET FOREIGN_KEY_CHECKS = 0;\n\n");

        // 导出每张表的结构和数据
        for (DatabaseBackupVO.TableInfo table : tables) {
            String tableName = table.getTableName();
            log.debug("正在导出表: {}", tableName);

            sql.append("-- ----------------------------------------------------------------\n");
            sql.append("-- Table: ").append(tableName).append("\n");
            sql.append("-- ----------------------------------------------------------------\n\n");

            // 获取表结构
            try {
                String createTableSql = getCreateTableStatement(tableName);
                sql.append("DROP TABLE IF EXISTS `").append(tableName).append("`;\n");
                sql.append(createTableSql).append(";\n\n");

                // 导出表数据
                String insertStatements = exportTableData(tableName);
                if (!insertStatements.isEmpty()) {
                    sql.append(insertStatements).append("\n");
                }
            } catch (Exception e) {
                log.warn("导出表 {} 失败: {}", tableName, e.getMessage());
                sql.append("-- ERROR exporting table: ").append(tableName).append(" - ").append(e.getMessage())
                        .append("\n\n");
            }
        }

        sql.append("SET FOREIGN_KEY_CHECKS = 1;\n");

        return sql.toString();
    }

    /**
     * 获取创建表的SQL语句
     */
    private String getCreateTableStatement(String tableName) {
        String sql = "SHOW CREATE TABLE `" + tableName + "`";
        Map<String, Object> result = jdbcTemplate.queryForMap(sql);
        return (String) result.get("Create Table");
    }

    /**
     * 导出表数据为INSERT语句
     */
    private String exportTableData(String tableName) {
        StringBuilder inserts = new StringBuilder();

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM `" + tableName + "`");

            if (rows.isEmpty()) {
                return "";
            }

            for (Map<String, Object> row : rows) {
                StringBuilder values = new StringBuilder();
                StringBuilder columns = new StringBuilder();

                boolean first = true;
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    if (!first) {
                        columns.append(", ");
                        values.append(", ");
                    }
                    first = false;

                    columns.append("`").append(entry.getKey()).append("`");

                    Object value = entry.getValue();
                    if (value == null) {
                        values.append("NULL");
                    } else if (value instanceof Number) {
                        values.append(value);
                    } else if (value instanceof Boolean) {
                        values.append((Boolean) value ? 1 : 0);
                    } else if (value instanceof LocalDateTime) {
                        values.append("'").append(
                                ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                .append("'");
                    } else if (value instanceof java.sql.Timestamp) {
                        values.append("'").append(((java.sql.Timestamp) value).toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("'");
                    } else if (value instanceof java.sql.Date) {
                        values.append("'").append(value).append("'");
                    } else if (value instanceof byte[]) {
                        values.append("X'").append(bytesToHex((byte[]) value)).append("'");
                    } else {
                        values.append("'").append(escapeSql(value.toString())).append("'");
                    }
                }

                inserts.append("INSERT INTO `").append(tableName).append("` (")
                        .append(columns).append(") VALUES (")
                        .append(values).append(");\n");
            }
        } catch (Exception e) {
            log.warn("导出表 {} 数据失败: {}", tableName, e.getMessage());
        }

        return inserts.toString();
    }

    /**
     * 字节数组转十六进制
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * SQL字符串转义
     */
    private String escapeSql(String value) {
        if (value == null)
            return "";
        return value
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\0", "");
    }

    private String extractDatabaseName(String url) {
        try {
            // jdbc:mysql://host:port/database?params
            String withoutParams = url.split("\\?")[0];
            String[] parts = withoutParams.split("/");
            return parts[parts.length - 1];
        } catch (Exception e) {
            log.warn("无法从URL提取数据库名: {}", url);
            return "unknown";
        }
    }

    private String getDatabaseVersion() {
        try {
            return jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
        } catch (Exception e) {
            log.warn("获取数据库版本失败", e);
            return "unknown";
        }
    }

    private List<DatabaseBackupVO.TableInfo> getTableInfoList(String databaseName) {
        List<DatabaseBackupVO.TableInfo> tables = new ArrayList<>();

        String sql = """
                SELECT
                    TABLE_NAME,
                    TABLE_COMMENT,
                    TABLE_ROWS,
                    DATA_LENGTH,
                    INDEX_LENGTH,
                    CREATE_TIME,
                    UPDATE_TIME
                FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = ?
                AND TABLE_TYPE = 'BASE TABLE'
                ORDER BY TABLE_NAME
                """;

        try {
            log.debug("查询数据库 {} 的表信息", databaseName);
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, databaseName);
            log.debug("查询到 {} 条记录", results.size());

            for (Map<String, Object> row : results) {
                try {
                    Long dataSize = row.get("DATA_LENGTH") != null ? ((Number) row.get("DATA_LENGTH")).longValue() : 0L;
                    Long indexSize = row.get("INDEX_LENGTH") != null ? ((Number) row.get("INDEX_LENGTH")).longValue()
                            : 0L;
                    Long rowCount = row.get("TABLE_ROWS") != null ? ((Number) row.get("TABLE_ROWS")).longValue() : 0L;

                    LocalDateTime createTime = parseDateTime(row.get("CREATE_TIME"));
                    LocalDateTime updateTime = parseDateTime(row.get("UPDATE_TIME"));

                    DatabaseBackupVO.TableInfo tableInfo = DatabaseBackupVO.TableInfo.builder()
                            .tableName((String) row.get("TABLE_NAME"))
                            .tableComment((String) row.get("TABLE_COMMENT"))
                            .rowCount(rowCount)
                            .dataSizeBytes(dataSize)
                            .dataSizeFormatted(formatBytes(dataSize))
                            .indexSizeBytes(indexSize)
                            .createTime(createTime)
                            .updateTime(updateTime)
                            .build();

                    tables.add(tableInfo);
                } catch (Exception e) {
                    log.warn("处理表信息行失败: {}, 错误: {}", row, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("获取表信息失败: {}", e.getMessage(), e);
        }

        return tables;
    }

    /**
     * 安全地解析日期时间
     */
    private LocalDateTime parseDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime();
        }
        return null;
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
}
