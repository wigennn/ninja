# 快速开始

本指南将帮助你快速搭建和运行DDD脚手架项目。

## 环境要求

- **JDK**: 17或更高版本
- **Maven**: 3.6或更高版本
- **IDE**: IntelliJ IDEA（推荐）或Eclipse
- **数据库**: H2（内存数据库，已包含）或MySQL/PostgreSQL

## 安装步骤

### 1. 克隆项目

```bash
git clone <repository-url>
cd ddd-scaffold
```

### 2. 构建项目

```bash
mvn clean install
```

### 3. 运行项目

```bash
cd ddd-bootstrap
mvn spring-boot:run
```

或者使用IDE直接运行`DddApplication`类。

### 4. 验证运行

访问以下地址验证项目是否正常运行：

- 应用健康检查：http://localhost:8080/actuator/health
- H2控制台：http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - 用户名: `sa`
  - 密码: （空）

## 测试API

### 使用curl测试

**创建用户**：
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "password123"
  }'
```

**查询用户**：
```bash
curl http://localhost:8080/api/users/1
```

**查询所有用户**：
```bash
curl http://localhost:8080/api/users
```

**更新用户**：
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newemail@example.com"
  }'
```

**删除用户**：
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

### 使用Postman

1. 导入API集合（如果有）
2. 或手动创建请求测试各个端点

## 项目结构说明

```
ddd-scaffold/
├── ddd-domain/          # 领域层：实体、值对象、领域服务
├── ddd-application/     # 应用层：应用服务、DTO、命令
├── ddd-infrastructure/  # 基础设施层：持久化、外部服务
├── ddd-interfaces/      # 接口层：REST控制器、请求/响应DTO
└── ddd-bootstrap/       # 启动层：Spring Boot配置
```

## 开发新功能

### 1. 创建新的聚合

假设要创建一个`Order`聚合：

**步骤1：在领域层创建实体**

```java
// ddd-domain/src/main/java/com/example/domain/model/order/Order.java
public class Order extends BaseEntity {
    // 领域逻辑
}
```

**步骤2：创建仓储接口**

```java
// ddd-domain/src/main/java/com/example/domain/repository/OrderRepository.java
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
}
```

**步骤3：在应用层创建应用服务**

```java
// ddd-application/src/main/java/com/example/application/service/OrderApplicationService.java
@Service
public class OrderApplicationService {
    // 应用服务方法
}
```

**步骤4：在基础设施层实现仓储**

```java
// ddd-infrastructure/src/main/java/com/example/infrastructure/persistence/repository/OrderRepositoryImpl.java
@Repository
public class OrderRepositoryImpl implements OrderRepository {
    // 实现
}
```

**步骤5：在接口层创建控制器**

```java
// ddd-interfaces/src/main/java/com/example/interfaces/rest/controller/OrderController.java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    // REST端点
}
```

### 2. 配置数据库

如果需要使用MySQL或PostgreSQL：

**修改application.yml**：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ddd_db
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

**添加依赖**（在`ddd-infrastructure/pom.xml`）：

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
```

## 常见问题

### 1. 编译错误

**问题**：MapStruct生成的代码找不到

**解决**：
- 确保IDE启用了注解处理器
- 在IntelliJ IDEA中：Settings → Build → Compiler → Annotation Processors → Enable annotation processing

### 2. 运行错误

**问题**：端口被占用

**解决**：修改`application.yml`中的端口：

```yaml
server:
  port: 8081
```

### 3. 数据库连接错误

**问题**：无法连接数据库

**解决**：
- 检查数据库配置
- 确保数据库服务已启动
- 检查用户名和密码

## 下一步

- 阅读[开发规范](development.md)了解编码规范
- 阅读[最佳实践](best-practices.md)学习DDD最佳实践
- 查看[示例代码](../examples/user-management.md)了解完整示例
- 阅读[架构文档](../architecture/overview.md)深入理解架构

## 获取帮助

- 查看[常见问题](../reference/faq.md)
- 提交Issue到项目仓库
- 阅读[DDD核心概念](../reference/ddd-concepts.md)

