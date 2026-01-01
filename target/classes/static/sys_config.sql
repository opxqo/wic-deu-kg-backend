-- ================================================================
-- 系统配置表
-- ================================================================

CREATE TABLE IF NOT EXISTS sys_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(64) NOT NULL UNIQUE COMMENT '配置键',
    config_value VARCHAR(255) NOT NULL COMMENT '配置值',
    description VARCHAR(255) COMMENT '描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 初始配置数据
INSERT INTO sys_config (config_key, config_value, description) VALUES
('maintenance_mode', 'false', '维护模式：开启后普通用户将无法访问系统'),
('open_registration', 'true', '开放注册：允许新用户自行注册账号')
ON DUPLICATE KEY UPDATE config_key = config_key;
