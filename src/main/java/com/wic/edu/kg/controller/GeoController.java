package com.wic.edu.kg.controller;

import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.config.GeoFenceConfig;
import com.wic.edu.kg.service.GeoFenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 地理围栏控制器
 * 提供位置校验相关接口
 */
@Tag(name = "地理围栏", description = "位置校验相关接口")
@RestController
@RequestMapping("/api/geo")
@RequiredArgsConstructor
public class GeoController {
    
    private final GeoFenceConfig geoFenceConfig;
    private final GeoFenceService geoFenceService;
    
    /**
     * 检查位置是否在允许范围内
     */
    @Operation(summary = "检查位置", description = "检查GPS坐标是否在校园范围内")
    @PostMapping("/check")
    public Result<GeoCheckResult> checkLocation(@RequestBody GeoCheckRequest request,
                                                 HttpServletRequest httpRequest) {
        GeoCheckResult result = new GeoCheckResult();
        
        // 如果地理围栏未启用
        if (!geoFenceConfig.isEnabled()) {
            result.setAllowed(true);
            result.setMessage("地理围栏未启用");
            result.setGeoFenceEnabled(false);
            return Result.success(result);
        }
        
        result.setGeoFenceEnabled(true);
        result.setCenterLat(geoFenceConfig.getCenterLatitude());
        result.setCenterLng(geoFenceConfig.getCenterLongitude());
        result.setAllowedRadiusMeters(geoFenceConfig.getRadiusMeters());
        
        // 获取客户端IP
        String clientIp = getClientIp(httpRequest);
        result.setClientIp(maskIp(clientIp));
        
        // IP白名单校验
        boolean ipAllowed = geoFenceService.isIpWhitelisted(clientIp);
        result.setIpAllowed(ipAllowed);
        
        // GPS位置校验
        boolean locationAllowed = false;
        if (request.getLatitude() != null && request.getLongitude() != null) {
            double distance = geoFenceService.calculateDistance(
                geoFenceConfig.getCenterLatitude(),
                geoFenceConfig.getCenterLongitude(),
                request.getLatitude(), 
                request.getLongitude());
            result.setDistanceMeters((int) Math.round(distance));
            
            locationAllowed = geoFenceService.isWithinCampus(
                request.getLatitude(), request.getLongitude());
            result.setLocationAllowed(locationAllowed);
        }
        
        // 判断最终结果：位置校验或IP校验任一通过即可
        boolean allowed = locationAllowed || ipAllowed;
        
        result.setAllowed(allowed);
        
        if (allowed) {
            result.setMessage("位置校验通过，欢迎使用校园服务！");
        } else {
            result.setMessage("抱歉，您当前不在校园范围内。部分功能可能受限。");
        }
        
        return Result.success(result);
    }
    
    /**
     * 获取地理围栏配置（不含敏感信息）
     */
    @Operation(summary = "获取配置", description = "获取地理围栏的公开配置信息")
    @GetMapping("/config")
    public Result<GeoConfigInfo> getConfig() {
        GeoConfigInfo config = new GeoConfigInfo();
        config.setEnabled(geoFenceConfig.isEnabled());
        config.setCenterLat(geoFenceConfig.getCenterLatitude());
        config.setCenterLng(geoFenceConfig.getCenterLongitude());
        config.setAllowedRadiusMeters(geoFenceConfig.getRadiusMeters());
        return Result.success(config);
    }
    
    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
    
    /**
     * 掩盖IP地址的部分信息
     */
    private String maskIp(String ip) {
        if (ip == null) return null;
        if (ip.contains(":")) {
            // IPv6
            return ip.substring(0, Math.min(ip.length(), 10)) + "::***";
        } else {
            // IPv4
            String[] parts = ip.split("\\.");
            if (parts.length == 4) {
                return parts[0] + "." + parts[1] + ".*.*";
            }
        }
        return ip;
    }
    
    // 请求对象
    @Data
    public static class GeoCheckRequest {
        private Double latitude;
        private Double longitude;
    }
    
    // 响应对象
    @Data
    public static class GeoCheckResult {
        private boolean allowed;
        private String message;
        private boolean geoFenceEnabled;
        private boolean locationAllowed;
        private boolean ipAllowed;
        private Integer distanceMeters;
        private Double centerLat;
        private Double centerLng;
        private Integer allowedRadiusMeters;
        private String clientIp;
    }
    
    // 配置信息对象
    @Data
    public static class GeoConfigInfo {
        private boolean enabled;
        private Double centerLat;
        private Double centerLng;
        private Integer allowedRadiusMeters;
    }
}
