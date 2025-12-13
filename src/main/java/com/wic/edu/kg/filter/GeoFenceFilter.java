package com.wic.edu.kg.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.config.GeoFenceConfig;
import com.wic.edu.kg.service.GeoFenceService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 地理围栏过滤器
 * 校验请求是否来自允许的地理位置或IP范围
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class GeoFenceFilter implements Filter {
    
    private final GeoFenceConfig geoFenceConfig;
    private final GeoFenceService geoFenceService;
    private final ObjectMapper objectMapper;
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        // 如果地理围栏未启用，直接放行
        if (!geoFenceConfig.isEnabled()) {
            chain.doFilter(request, response);
            return;
        }
        
        String requestPath = request.getRequestURI();
        
        // 检查是否是白名单路径
        if (geoFenceService.isPathWhitelisted(requestPath)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 获取客户端IP
        String clientIp = getClientIp(request);
        
        // 获取GPS坐标（从请求头中获取）
        String latitudeStr = request.getHeader("X-Latitude");
        String longitudeStr = request.getHeader("X-Longitude");
        
        boolean locationCheckPassed = false;
        boolean ipCheckPassed = false;
        
        // IP白名单校验
        ipCheckPassed = geoFenceService.isIpWhitelisted(clientIp);
        if (ipCheckPassed) {
            log.debug("IP白名单校验通过: {}", clientIp);
        }
        
        // GPS位置校验
        if (latitudeStr != null && longitudeStr != null) {
            try {
                double latitude = Double.parseDouble(latitudeStr);
                double longitude = Double.parseDouble(longitudeStr);
                locationCheckPassed = geoFenceService.isWithinCampus(latitude, longitude);
                
                if (locationCheckPassed) {
                    double distance = geoFenceService.calculateDistance(
                        geoFenceConfig.getCenterLatitude(),
                        geoFenceConfig.getCenterLongitude(),
                        latitude, longitude);
                    log.debug("位置校验通过 - 坐标: ({}, {}), 距离校园中心: {}m", 
                              latitude, longitude, (int) distance);
                } else {
                    double distance = geoFenceService.calculateDistance(
                        geoFenceConfig.getCenterLatitude(),
                        geoFenceConfig.getCenterLongitude(),
                        latitude, longitude);
                    log.info("位置校验失败 - 坐标: ({}, {}), 距离校园中心: {}m, 允许范围: {}m", 
                             latitude, longitude, (int) distance, geoFenceConfig.getRadiusMeters());
                }
            } catch (NumberFormatException e) {
                log.warn("无效的GPS坐标格式: lat={}, lng={}", latitudeStr, longitudeStr);
            }
        }
        
        // 判断是否通过校验（位置校验或IP校验任一通过即可）
        boolean allowed = locationCheckPassed || ipCheckPassed;
        
        if (allowed) {
            chain.doFilter(request, response);
        } else {
            log.info("地理围栏拒绝访问 - IP: {}, 路径: {}, 位置: ({}, {})", 
                     clientIp, requestPath, latitudeStr, longitudeStr);
            
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            
            Result<?> result = Result.error(403, "抱歉，本服务仅限校园内用户使用。请确保您在校园范围内访问。");
            response.getWriter().write(objectMapper.writeValueAsString(result));
        }
    }
    
    /**
     * 获取客户端真实IP地址
     * 考虑代理和负载均衡的情况
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 如果有多个IP（经过多级代理），取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}
