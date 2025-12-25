package com.wic.edu.kg.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j (OpenAPI 3.0) 配置类
 * 访问地址: http://localhost:8080/doc.html
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WIC EDU KG API 文档")
                        .description("武汉城市学院知识图谱后端 API 接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("WIC Team")
                                .email("contact@wic.edu.kg")
                                .url("https://wic.edu.kg"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .schemaRequirement("Bearer", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Token 认证，格式: Bearer {token}"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"));
    }
}
