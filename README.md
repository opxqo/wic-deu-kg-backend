# WIC.EDU.KG 后端服务

武汉城市学院教务服务平台后端 API，基于 Spring Boot 3.2 + MyBatis-Plus 构建。

## 🛠️ 技术栈

| 类别 | 技术 |
|------|------|
| 框架 | Spring Boot 3.2.5, Spring Security |
| ORM | MyBatis-Plus 3.5.5 |
| 数据库 | MySQL 8.0 |
| 缓存 | Caffeine |
| 认证 | JWT (jjwt 0.12.3) |
| 文档 | Knife4j (OpenAPI 3) |
| 存储 | Cloudflare R2 (AWS S3 SDK) |
| 构建 | Maven, Java 21 |

## 📦 功能模块

### 用户模块
- 用户注册/登录/激活
- JWT 令牌认证
- 邮箱验证码
- 三级权限体系（组织者 > 管理员 > 普通用户）

### 学部管理
- 学部信息展示
- 辅导员信息管理

### 光影城院（图片库）
- 图片上传/浏览/点赞
- 分类管理
- 审核系统

### 美食指南
- 店铺管理
- 商品展示
- 评论/点赞

### 学长学姐说（留言板）
- 留言发布/删除
- 自定义便签样式
- 点赞功能
- 审核系统

### 实时聊天
- WebSocket 通信
- 消息持久化

## 🚀 快速开始

### 环境要求
- JDK 21+
- Maven 3.8+
- MySQL 8.0+

### 配置

在 `src/main/resources/application.yml` 中配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wic_edu_kg
    username: your_username
    password: your_password
  mail:
    host: smtp.example.com
    username: your_email
    password: your_password

jwt:
  secret: your_jwt_secret
  expiration: 604800000

r2:
  access-key: your_r2_access_key
  secret-key: your_r2_secret_key
  endpoint: your_r2_endpoint
  bucket: your_bucket_name
```

### 构建与运行

```bash
# 开发环境
mvn spring-boot:run

# 生产构建
mvn clean package -DskipTests
java -jar target/wic-edu-kg-backend-1.0.0.jar
```

## 📝 API 文档

启动后访问 Knife4j 文档界面：

- **Knife4j UI**: http://localhost:8080/doc.html
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🔐 权限体系

| 角色 | 级别 | 权限范围 |
|------|------|----------|
| ORGANIZER | 1 | 最高权限：系统管理、角色分配 |
| ADMIN | 2 | 管理权限：内容审核、用户管理 |
| USER | 3 | 基础权限：个人操作 |

## 📂 项目结构

```
src/main/java/com/wic/edu/kg/
├── annotation/     # 自定义注解
├── aspect/         # AOP 切面
├── common/         # 通用类
├── config/         # 配置类
├── controller/     # REST 控制器
├── dto/            # 数据传输对象
├── entity/         # 实体类
├── enums/          # 枚举类
├── exception/      # 异常处理
├── filter/         # 过滤器
├── mapper/         # MyBatis Mapper
├── service/        # 业务逻辑
├── utils/          # 工具类
└── vo/             # 视图对象
```

## 🤝 贡献

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 发起 Pull Request

## 📄 许可证

本项目采用 Apache 2.0 许可证。
