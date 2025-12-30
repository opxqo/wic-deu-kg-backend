package com.wic.edu.kg.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * RESTful API 安全配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // ==================== 认证相关 ====================

                        // 会话管理（登录）
                        .requestMatchers(HttpMethod.POST, "/api/sessions").permitAll()

                        // 用户注册
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                        // 用户激活和密码重置（无需登录）
                        .requestMatchers(HttpMethod.PATCH, "/api/users/me/status").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/users/me/password").permitAll()

                        // 验证码
                        .requestMatchers(HttpMethod.POST, "/api/verification-codes/**").permitAll()

                        // ==================== 公开资源 ====================

                        // 用户公开信息
                        .requestMatchers(HttpMethod.GET, "/api/users/public/**").permitAll()

                        // 学部（全公开）
                        .requestMatchers(HttpMethod.GET, "/api/departments/**").permitAll()

                        // 图片库（浏览公开）
                        .requestMatchers(HttpMethod.GET, "/api/images").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/images/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/image-categories").permitAll()

                        // 店铺和商品（浏览公开）
                        .requestMatchers(HttpMethod.GET, "/api/stores/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/{id}/comments").permitAll()

                        // 留言板（浏览公开）
                        .requestMatchers(HttpMethod.GET, "/api/messages").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/messages/fonts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/messages/user/*").permitAll()

                        // 学院动态（浏览公开）
                        .requestMatchers(HttpMethod.GET, "/api/article").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/article/*").permitAll()

                        // 地理围栏
                        .requestMatchers("/api/geo/**").permitAll()

                        // ==================== 静态资源与文档 ====================
                        .requestMatchers("/ws/**", "/error").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", "/favicon.ico")
                        .permitAll()
                        .requestMatchers("/api-test.html", "/static/**", "/*.html", "/*.css", "/*.js").permitAll()

                        // ==================== 其他接口需认证 ====================
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
