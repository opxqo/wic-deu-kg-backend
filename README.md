# WIC.EDU.KG 后端服务

这是 WIC（World Information Center）教育知识图谱项目的后端服务，基于 Spring Boot 和 MyBatis-Plus 实现。

主要内容：
- 语言：Java
- 构建工具：Maven
- 入口类：`com.wic.edu.kg.WicEduKgApplication`

快速开始

1. 构建：

```bash
mvn clean package -DskipTests
```

2. 运行：

```bash
java -jar target/wic-deu-kg-backend-*.jar
```

配置

- 在 `src/main/resources/application.yml` 中配置数据库、JWT 和其他环境相关设置。

贡献

如需贡献，请先 fork 并发起 Pull Request。确保遵循现有编码风格并添加必要的说明。

许可证

请在合并前附上合适的许可证文件（如 Apache-2.0）。
