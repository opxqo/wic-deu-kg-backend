package com.wic.edu.kg.service.impl;

import com.wic.edu.kg.entity.SysConfig;
import com.wic.edu.kg.mapper.SysConfigMapper;
import com.wic.edu.kg.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统配置服务实现
 * 使用本地缓存减少数据库查询
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    // 配置缓存（简单内存缓存）
    private final Map<String, String> configCache = new ConcurrentHashMap<>();
    private volatile long lastCacheRefresh = 0;
    private static final long CACHE_TTL_MS = 30000; // 30秒缓存

    // 配置键常量
    public static final String KEY_MAINTENANCE_MODE = "maintenance_mode";
    public static final String KEY_OPEN_REGISTRATION = "open_registration";

    @Override
    public List<SysConfig> getAllConfigs() {
        return sysConfigMapper.selectList(null);
    }

    @Override
    public String getValue(String key) {
        // 检查缓存是否过期
        if (System.currentTimeMillis() - lastCacheRefresh > CACHE_TTL_MS) {
            refreshCache();
        }

        String cachedValue = configCache.get(key);
        if (cachedValue != null) {
            return cachedValue;
        }

        // 缓存未命中，查询数据库
        String value = sysConfigMapper.getValueByKey(key);
        if (value != null) {
            configCache.put(key, value);
        }
        return value;
    }

    @Override
    public boolean getBooleanValue(String key, boolean defaultValue) {
        String value = getValue(key);
        if (value == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    @Override
    public boolean updateValue(String key, String value) {
        int rows = sysConfigMapper.updateValueByKey(key, value);
        if (rows > 0) {
            configCache.put(key, value);
            log.info("系统配置已更新: {} = {}", key, value);
            return true;
        }
        return false;
    }

    @Override
    public void updateConfigs(Map<String, String> configs) {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            updateValue(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean isMaintenanceMode() {
        return getBooleanValue(KEY_MAINTENANCE_MODE, false);
    }

    @Override
    public boolean isOpenRegistration() {
        return getBooleanValue(KEY_OPEN_REGISTRATION, true);
    }

    @Override
    public void setMaintenanceMode(boolean enabled) {
        updateValue(KEY_MAINTENANCE_MODE, String.valueOf(enabled));
    }

    @Override
    public void setOpenRegistration(boolean enabled) {
        updateValue(KEY_OPEN_REGISTRATION, String.valueOf(enabled));
    }

    /**
     * 刷新缓存
     */
    private void refreshCache() {
        try {
            List<SysConfig> configs = sysConfigMapper.selectList(null);
            configCache.clear();
            for (SysConfig config : configs) {
                configCache.put(config.getConfigKey(), config.getConfigValue());
            }
            lastCacheRefresh = System.currentTimeMillis();
            log.debug("系统配置缓存已刷新，共 {} 项", configs.size());
        } catch (Exception e) {
            log.warn("刷新配置缓存失败: {}", e.getMessage());
        }
    }
}
