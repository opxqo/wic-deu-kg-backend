package com.wic.edu.kg.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wic.edu.kg.common.ApiResponse;
import com.wic.edu.kg.service.SysConfigService;
import com.wic.edu.kg.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * 维护模式过滤器
 * 当开启维护模式时，非管理员用户将无法访问系统
 */
@Slf4j
@Component
@Order(1)
public class MaintenanceModeFilter extends OncePerRequestFilter {

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 维护模式下也允许访问的路径
    private static final Set<String> ALLOWED_PATHS = Set.of(
            "/api/sessions", // 登录
            "/api/admin/config", // 配置管理
            "/api/admin/logs", // 日志
            "/api/admin/database", // 数据库备份
            "/swagger-ui",
            "/v3/api-docs",
            "/doc.html",
            "/error");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 检查是否开启维护模式
        if (!sysConfigService.isMaintenanceMode()) {
            chain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();

        // 检查是否为允许的路径
        if (isAllowedPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 检查用户是否为管理员
        if (isAdminUser(request)) {
            chain.doFilter(request, response);
            return;
        }

        // 非管理员用户返回维护模式响应
        log.info("维护模式: 拒绝访问 {} (用户未认证或非管理员)", path);
        sendMaintenanceResponse(response, path);
    }

    private boolean isAllowedPath(String path) {
        for (String allowedPath : ALLOWED_PATHS) {
            if (path.startsWith(allowedPath)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAdminUser(HttpServletRequest request) {
        String authHeader = request.getHeader(tokenHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        try {
            String token = authHeader.substring(7);
            String role = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));
            // ORGANIZER 和 ADMIN 可以在维护模式下访问
            return "ORGANIZER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
        } catch (Exception e) {
            log.debug("解析用户角色失败: {}", e.getMessage());
            return false;
        }
    }

    private void sendMaintenanceResponse(HttpServletResponse response, String path) throws IOException {
        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<?> apiResponse = ApiResponse.error(
                "SERVICE_UNAVAILABLE",
                "系统维护中，请稍后再试",
                path);

        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
