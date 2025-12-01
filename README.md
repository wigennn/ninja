# DDD领域驱动设计Maven脚手架

一个完整的、生产就绪的DDD（领域驱动设计）架构实现，帮助开发者快速搭建符合领域驱动设计原则的企业级应用。

## 项目特点

- ✅ **完整的分层架构**：领域层、应用层、基础设施层、接口层
- ✅ **清晰的职责划分**：每层职责明确，符合DDD原则
- ✅ **示例代码**：包含完整的用户管理示例
- ✅ **现代化技术栈**：Spring Boot 3.2、Java 17、MapStruct、Lombok
- ✅ **完善的文档**：详细的架构说明和使用指南

## 🚀 快速开始

### 使用此模板创建新项目

**推荐方式：使用脚本**
```bash
./scripts/create-project.sh my-new-project com.mycompany.myproject
```

**或查看详细指南：** [快速开始指南](./QUICK_START.md) | [完整使用指南](./docs/guide/create-new-project.md)

---

### 运行模板项目（测试用）

#### 环境要求

- JDK 17+
- Maven 3.6+
- IDE（推荐IntelliJ IDEA）

#### 构建项目

```bash
mvn clean install
```

#### 运行项目

```bash
cd ddd-bootstrap
mvn spring-boot:run
```

### 访问应用

- 应用地址：http://localhost:8080
- H2控制台：http://localhost:8080/h2-console
- API端点：http://localhost:8080/api/users

## 项目结构

```
ddd-scaffold/
├── ddd-domain/          # 领域层：实体、值对象、领域服务、仓储接口
├── ddd-application/     # 应用层：应用服务、DTO、命令
├── ddd-infrastructure/  # 基础设施层：持久化、外部服务
├── ddd-interfaces/      # 接口层：REST控制器、请求/响应DTO
├── ddd-bootstrap/       # 启动层：Spring Boot配置
└── docs/                # GitBook文档
```

## 技术栈

- **Java 17**：编程语言
- **Spring Boot 3.2**：应用框架
- **Spring Data JPA**：数据访问
- **H2 Database**：内存数据库（可替换）
- **MapStruct**：对象映射
- **Lombok**：减少样板代码
- **Maven**：项目构建

## 文档

详细的文档请查看 [docs](./docs/) 目录。

### 本地查看文档

**快速启动本地文档服务器：**
```bash
./scripts/serve-docs.sh
```

**或手动启动：**
```bash
# 安装HonKit（推荐）
npm install -g honkit

# 启动服务器
cd docs
honkit serve
```

访问：http://localhost:4000

更多方法请查看：[本地查看文档指南](./docs/README_LOCAL.md)

### 文档目录

- [架构概览](./docs/architecture/overview.md)
- [快速开始](./docs/guide/getting-started.md)
- [开发规范](./docs/guide/development.md)
- [最佳实践](./docs/guide/best-practices.md)
- [DDD核心概念](./docs/reference/ddd-concepts.md)

## API示例

### 创建用户

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 查询用户

```bash
curl http://localhost:8080/api/users/1
```

### 查询所有用户

```bash
curl http://localhost:8080/api/users
```

## 使用模板创建新项目

**想要基于此模板创建新项目？** 查看详细指南：

👉 [使用模板创建新项目指南](./docs/guide/create-new-project.md)

快速步骤：
1. 复制整个项目目录
2. 修改POM文件中的groupId、artifactId
3. 替换所有包名（`com.example` → `com.yourcompany.yourproject`）
4. 清理示例代码（可选）
5. 运行 `mvn clean install` 验证

## 开发新功能

参考 [快速开始指南](./docs/guide/getting-started.md) 了解如何添加新的聚合和功能。

## 贡献

欢迎提交Issue和Pull Request！

## 许可证

MIT License

