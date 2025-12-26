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
                                                .description("武汉城市学院知识图谱后端 API 接口文档\n\n" +
                                                                "### 🔐 认证说明\n" +
                                                                "1. 访问 `POST /api/auth/login` 接口进行登录。\n" +
                                                                "2. 复制返回的 `token` 字符串。\n" +
                                                                "3. 点击页面右上角的 **Authorize** 按钮。\n" +
                                                                "4. 在弹出框中输入 `Bearer ` + `token` (注意中间有空格)，例如: `Bearer eyJhbGci...`\n"
                                                                +
                                                                "5. 点击 **Authorize** 确认，之后的所有请求都会自动携带 Token。\n\n" +
                                                                "### 📌 常用功能\n" +
                                                                "- **用户名片**: `GET /api/auth/public/users/card/{studentId}` (无需登录)\n"
                                                                +
                                                                "- **图片库**: `GET /api/gallery` (无需登录)\n" +
                                                                "- **美食**: `GET /api/food/stores` (无需登录)")
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
