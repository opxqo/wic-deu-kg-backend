package com.wic.edu.kg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * 地理围栏配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.geofence")
public class GeoFenceConfig {
    
    /**
     * 是否启用地理围栏
     */
    private boolean enabled = true;
    
    /**
     * 校园中心纬度
     */
    private double centerLatitude = 30.4657;
    
    /**
     * 校园中心经度
     */
    private double centerLongitude = 114.3965;
    
    /**
     * 允许访问的半径（米）
     */
    private int radiusMeters = 5000;
    
    /**
     * 白名单 IP（逗号分隔）
     */
    private String whitelistIps = "127.0.0.1,::1";
    
    /**
     * 白名单路径（逗号分隔）
     */
    private String whitelistPaths = "/api/auth/login,/api/auth/register";
    
    /**
     * 获取白名单 IP 列表
     */
    public List<String> getWhitelistIpList() {
        return Arrays.asList(whitelistIps.split(","));
    }
    
    /**
     * 获取白名单路径列表
     */
    public List<String> getWhitelistPathList() {
        return Arrays.asList(whitelistPaths.split(","));
    }
}
