package com.wic.edu.kg.service;

import com.wic.edu.kg.vo.DatabaseBackupVO;

/**
 * 数据库备份服务接口
 */
public interface DatabaseBackupService {

    /**
     * 获取数据库备份信息
     * 
     * @return 数据库备份信息
     */
    DatabaseBackupVO getDatabaseBackupInfo();

    /**
     * 备份数据库并上传到R2云存储
     * 
     * @return 包含备份文件URL的备份信息
     */
    DatabaseBackupVO backupAndUploadToR2();
}
