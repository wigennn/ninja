# 技术栈说明

本文档介绍项目使用的技术栈及其作用。

## 核心技术

### Java 17

**作用**：编程语言

**选择理由**：
- LTS版本，长期支持
- 性能优秀
- 语法现代化（Record、Pattern Matching等）

### Spring Boot 3.2

**作用**：应用框架

**主要功能**：
- 依赖注入
- 自动配置
- Web框架
- 数据访问

**版本**：3.2.0

### Maven

**作用**：项目构建和依赖管理

**主要功能**：
- 依赖管理
- 项目构建
- 多模块管理

## 数据访问

### Spring Data JPA

**作用**：简化数据访问

**主要功能**：
- 自动生成Repository实现
- 查询方法自动实现
- 事务管理

**版本**：随Spring Boot

### H2 Database

**作用**：内存数据库（开发/测试）

**主要功能**：
- 快速启动
- 无需安装
- 适合开发和测试

**生产环境**：可替换为MySQL、PostgreSQL等

## 对象映射

### MapStruct

**作用**：对象转换

**主要功能**：
- 编译时生成转换代码
- 类型安全
- 性能优秀

**版本**：1.5.5.Final

**使用场景**：
- 领域对象 ↔ DTO
- 领域对象 ↔ 持久化对象

### Lombok

**作用**：减少样板代码

**主要功能**：
- 自动生成getter/setter
- 自动生成构造函数
- 自动生成Builder

**版本**：1.18.30

## 验证

### Bean Validation (Jakarta Validation)

**作用**：参数验证

**主要功能**：
- 声明式验证
- 丰富的验证注解
- 自定义验证器

**使用场景**：
- 请求参数验证
- 命令对象验证

## Web框架

### Spring Web

**作用**：REST API支持

**主要功能**：
- REST控制器
- HTTP消息转换
- 异常处理

## 监控

### Spring Boot Actuator

**作用**：应用监控

**主要功能**：
- 健康检查
- 指标收集
- 应用信息

## 开发工具

### IDE支持

**推荐**：
- IntelliJ IDEA（推荐）
- Eclipse

**配置**：
- 启用注解处理器（MapStruct、Lombok）
- 安装Lombok插件

## 测试框架

### JUnit 5

**作用**：单元测试框架

**版本**：5.10.1

### Mockito

**作用**：Mock框架

**版本**：5.7.0

## 版本管理

所有版本在父POM中统一管理：

```xml
<properties>
    <java.version>17</java.version>
    <spring-boot.version>3.2.0</spring-boot.version>
    <lombok.version>1.18.30</lombok.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
    <junit.version>5.10.1</junit.version>
    <mockito.version>5.7.0</mockito.version>
</properties>
```

## 技术选型理由

### 为什么选择Spring Boot？

- **成熟稳定**：广泛使用，社区活跃
- **自动配置**：减少配置工作
- **生态丰富**：大量集成组件
- **文档完善**：官方文档详细

### 为什么选择MapStruct？

- **性能优秀**：编译时生成，运行时无反射
- **类型安全**：编译期检查
- **易于使用**：注解驱动

### 为什么选择Lombok？

- **减少样板代码**：自动生成getter/setter等
- **提高可读性**：代码更简洁
- **广泛使用**：Java社区标准工具

### 为什么选择H2？

- **快速启动**：无需安装数据库
- **适合开发**：快速验证功能
- **易于替换**：可轻松切换到其他数据库

## 生产环境建议

### 数据库

**推荐**：
- MySQL 8.0+
- PostgreSQL 14+

**配置示例**（MySQL）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ddd_db
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate  # 生产环境使用validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

### 日志

**推荐**：
- Logback（Spring Boot默认）
- Log4j2

### 监控

**推荐**：
- Prometheus + Grafana
- Spring Boot Actuator + Micrometer

### API文档

**推荐**：
- SpringDoc OpenAPI（Swagger）
- 自动生成API文档

## 升级建议

### 定期升级

- **Spring Boot**：关注LTS版本
- **Java**：关注LTS版本
- **其他依赖**：定期检查安全更新

### 升级步骤

1. 查看升级指南
2. 更新版本号
3. 运行测试
4. 检查破坏性变更
5. 逐步升级

## 总结

本技术栈选择遵循以下原则：

1. **成熟稳定**：选择经过验证的技术
2. **性能优秀**：选择高性能的解决方案
3. **易于使用**：选择易于学习和使用的工具
4. **社区活跃**：选择有活跃社区支持的项目
5. **文档完善**：选择有完善文档的技术

通过这些技术，可以快速构建高质量的企业级应用。

