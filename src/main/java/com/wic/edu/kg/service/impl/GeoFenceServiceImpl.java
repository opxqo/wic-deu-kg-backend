package com.wic.edu.kg.service.impl;

import com.wic.edu.kg.config.GeoFenceConfig;
import com.wic.edu.kg.service.GeoFenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地理围栏服务实现
 */
@Slf4j
@Service
public class GeoFenceServiceImpl implements GeoFenceService {
    
    @Autowired
    private GeoFenceConfig geoFenceConfig;
    
    // 地球半径（米）
    private static final double EARTH_RADIUS = 6371000;
    
    @Override
    public boolean isWithinCampus(double latitude, double longitude) {
        if (!geoFenceConfig.isEnabled()) {
            return true;
        }
        
        double distance = calculateDistance(
            geoFenceConfig.getCenterLatitude(),
            geoFenceConfig.getCenterLongitude(),
            latitude,
            longitude
        );
        
        boolean within = distance <= geoFenceConfig.getRadiusMeters();
        
        log.debug("位置检查: ({}, {}) 距校园中心 {:.0f}m, 允许范围 {}m, 结果: {}", 
            latitude, longitude, distance, geoFenceConfig.getRadiusMeters(), within ? "允许" : "拒绝");
        
        return within;
    }
    
    @Override
    public boolean isIpWhitelisted(String ip) {
        if (!geoFenceConfig.isEnabled()) {
            return true;
        }
        
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        
        List<String> whitelistIps = geoFenceConfig.getWhitelistIpList();
        
        for (String pattern : whitelistIps) {
            pattern = pattern.trim();
            if (pattern.isEmpty()) continue;
            
            // 精确匹配
            if (pattern.equals(ip)) {
                return true;
            }
            
            // CIDR 匹配 (如 192.168.0.0/16)
            if (pattern.contains("/")) {
                if (isIpInCidr(ip, pattern)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public boolean isPathWhitelisted(String path) {
        if (!geoFenceConfig.isEnabled()) {
            return true;
        }
        
        if (path == null || path.isEmpty()) {
            return false;
        }
        
        List<String> whitelistPaths = geoFenceConfig.getWhitelistPathList();
        
        for (String pattern : whitelistPaths) {
            pattern = pattern.trim();
            if (pattern.isEmpty()) continue;
            
            if (path.startsWith(pattern) || path.equals(pattern)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 使用 Haversine 公式计算球面距离
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
    
    /**
     * 检查 IP 是否在 CIDR 范围内
     */
    private boolean isIpInCidr(String ip, String cidr) {
        try {
            String[] parts = cidr.split("/");
            if (parts.length != 2) return false;
            
            String network = parts[0];
            int prefix = Integer.parseInt(parts[1]);
            
            // 处理 IPv4
            if (ip.contains(".") && network.contains(".")) {
                long ipLong = ipToLong(ip);
                long networkLong = ipToLong(network);
                long mask = (-1L) << (32 - prefix);
                
                return (ipLong & mask) == (networkLong & mask);
            }
        } catch (Exception e) {
            log.warn("CIDR 解析失败: {}", cidr, e);
        }
        
        return false;
    }
    
    /**
     * IPv4 地址转 long
     */
    private long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = result << 8 | Integer.parseInt(parts[i]);
        }
        return result;
    }
}
