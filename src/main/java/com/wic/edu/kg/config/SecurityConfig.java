package com.wic.edu.kg.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.web.filter.CorsFilter;

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
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/check-student-id",
                                "/api/auth/check-username",
                                "/api/auth/check-email",
                                "/api/auth/reset-password",
                                "/api/auth/send-activation-code",
                                "/api/auth/activate",
                                "/api/auth/send-reset-code",
                                "/api/auth/reset-password-with-code",
                                "/api/auth/public/users/card/**")
                        .permitAll()
                        .requestMatchers("/api/food/stores/**", "/api/food/products/*/comments").permitAll() // 美食模块公开接口
                        .requestMatchers("/api/departments/**").permitAll() // 学部模块公开接口
                        .requestMatchers("/api/public/**").permitAll() // 统一公开接口
                        .requestMatchers("/api/gallery", "/api/gallery/featured", "/api/gallery/categories",
                                "/api/gallery/{id}")
                        .permitAll() // 图片库公开接口
                        .requestMatchers("/ws/**", "/error").permitAll() // Allow websocket
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() // Allow
                                                                                                              // Swagger
                                                                                                              // UI
                        .requestMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", "/favicon.ico")
                        .permitAll() // Allow Knife4j
                        .requestMatchers("/api-test.html", "/static/**", "/*.html", "/*.css", "/*.js").permitAll() // Allow
                                                                                                                   // static
                                                                                                                   // files
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
