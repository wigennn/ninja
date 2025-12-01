# DDD领域驱动设计脚手架

欢迎使用DDD领域驱动设计Maven脚手架项目！

本项目提供了一个完整的、生产就绪的DDD架构实现，帮助开发者快速搭建符合领域驱动设计原则的企业级应用。

## 项目特点

- ✅ **完整的分层架构**：领域层、应用层、基础设施层、接口层
- ✅ **清晰的职责划分**：每层职责明确，符合DDD原则
- ✅ **示例代码**：包含完整的用户管理示例
- ✅ **现代化技术栈**：Spring Boot 3.2、Java 17、MapStruct、Lombok
- ✅ **完善的文档**：详细的架构说明和使用指南

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- IDE（推荐IntelliJ IDEA或Eclipse）

### 构建项目

```bash
mvn clean install
```

### 运行项目

```bash
cd ddd-bootstrap
mvn spring-boot:run
```

### 访问应用

- 应用地址：http://localhost:8080
- H2控制台：http://localhost:8080/h2-console
- API文档：http://localhost:8080/api/users

## 项目结构

```
ddd-scaffold/
├── ddd-domain/          # 领域层
├── ddd-application/     # 应用层
├── ddd-infrastructure/   # 基础设施层
├── ddd-interfaces/       # 接口层
└── ddd-bootstrap/        # 启动层
```

## 文档导航

- [架构概览](architecture/overview.md)
- [领域层设计](architecture/domain-layer.md)
- [应用层设计](architecture/application-layer.md)
- [基础设施层设计](architecture/infrastructure-layer.md)
- [接口层设计](architecture/interfaces-layer.md)
- [开发指南](guide/development.md)
- [最佳实践](guide/best-practices.md)

## 贡献

欢迎提交Issue和Pull Request！

## 许可证

MIT License

