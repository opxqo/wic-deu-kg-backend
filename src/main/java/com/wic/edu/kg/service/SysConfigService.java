package com.wic.edu.kg.service;

import com.wic.edu.kg.entity.SysConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 */
public interface SysConfigService {

    /**
     * 获取所有配置
     */
    List<SysConfig> getAllConfigs();

    /**
     * 获取配置值
     */
    String getValue(String key);

    /**
     * 获取布尔类型配置
     */
    boolean getBooleanValue(String key, boolean defaultValue);

    /**
     * 更新配置值
     */
    boolean updateValue(String key, String value);

    /**
     * 批量更新配置
     */
    void updateConfigs(Map<String, String> configs);

    /**
     * 是否开启维护模式
     */
    boolean isMaintenanceMode();

    /**
     * 是否开放注册
     */
    boolean isOpenRegistration();

    /**
     * 设置维护模式
     */
    void setMaintenanceMode(boolean enabled);

    /**
     * 设置开放注册
     */
    void setOpenRegistration(boolean enabled);
}
