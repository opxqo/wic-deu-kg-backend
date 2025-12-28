package com.wic.edu.kg.controller;

import com.wic.edu.kg.common.ApiResponse;
import com.wic.edu.kg.config.GeoFenceConfig;
import com.wic.edu.kg.service.GeoFenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 地理围栏资源控制器
 * 
 * RESTful 设计：
 * - POST /api/geo/check - 检查位置
 * - GET /api/geo/config - 获取配置
 */
@Tag(name = "地理围栏", description = "位置校验相关接口")
@RestController
@RequestMapping("/api/geo")
@RequiredArgsConstructor
public class GeoController {

    private final GeoFenceConfig geoFenceConfig;
    private final GeoFenceService geoFenceService;

    @PostMapping("/check")
    @Operation(summary = "检查位置", description = "检查GPS坐标是否在校园范围内")
    public ResponseEntity<ApiResponse<GeoCheckResult>> checkLocation(
            @RequestBody GeoCheckRequest request,
            HttpServletRequest httpRequest) {

        GeoCheckResult result = new GeoCheckResult();

        if (!geoFenceConfig.isEnabled()) {
            result.setAllowed(true);
            result.setMessage("地理围栏未启用");
            result.setGeoFenceEnabled(false);
            return ResponseEntity.ok(ApiResponse.ok(result));
        }

        result.setGeoFenceEnabled(true);
        result.setCenterLat(geoFenceConfig.getCenterLatitude());
        result.setCenterLng(geoFenceConfig.getCenterLongitude());
        result.setAllowedRadiusMeters(geoFenceConfig.getRadiusMeters());

        String clientIp = getClientIp(httpRequest);
        result.setClientIp(maskIp(clientIp));

        boolean ipAllowed = geoFenceService.isIpWhitelisted(clientIp);
        result.setIpAllowed(ipAllowed);

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

        boolean allowed = locationAllowed || ipAllowed;
        result.setAllowed(allowed);
        result.setMessage(allowed ? "位置校验通过，欢迎使用校园服务！" : "抱歉，您当前不在校园范围内。部分功能可能受限。");

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/config")
    @Operation(summary = "获取配置", description = "获取地理围栏的公开配置信息")
    public ResponseEntity<ApiResponse<GeoConfigInfo>> getConfig() {
        GeoConfigInfo config = new GeoConfigInfo();
        config.setEnabled(geoFenceConfig.isEnabled());
        config.setCenterLat(geoFenceConfig.getCenterLatitude());
        config.setCenterLng(geoFenceConfig.getCenterLongitude());
        config.setAllowedRadiusMeters(geoFenceConfig.getRadiusMeters());
        return ResponseEntity.ok(ApiResponse.ok(config));
    }

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

    private String maskIp(String ip) {
        if (ip == null)
            return null;
        if (ip.contains(":")) {
            return ip.substring(0, Math.min(ip.length(), 10)) + "::***";
        } else {
            String[] parts = ip.split("\\.");
            if (parts.length == 4) {
                return parts[0] + "." + parts[1] + ".*.*";
            }
        }
        return ip;
    }

    @Data
    public static class GeoCheckRequest {
        private Double latitude;
        private Double longitude;
    }

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

    @Data
    public static class GeoConfigInfo {
        private boolean enabled;
        private Double centerLat;
        private Double centerLng;
        private Integer allowedRadiusMeters;
    }
}
