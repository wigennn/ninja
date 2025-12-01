# 常见问题

本文档收集了项目使用过程中的常见问题及解答。

## 编译问题

### Q: MapStruct生成的代码找不到

**A**: 确保IDE启用了注解处理器：

**IntelliJ IDEA**：
1. Settings → Build, Execution, Deployment → Compiler → Annotation Processors
2. 勾选 "Enable annotation processing"
3. 重启IDE

**Eclipse**：
1. Project → Properties → Java Build Path → Annotation Processing
2. 启用注解处理

### Q: Lombok不生效

**A**: 
1. 确保安装了Lombok插件
2. 确保IDE启用了注解处理器
3. 重启IDE

### Q: 编译错误：找不到符号

**A**: 
1. 运行 `mvn clean install` 重新编译
2. 检查IDE是否正确导入项目
3. 刷新Maven项目

## 运行问题

### Q: 端口被占用

**A**: 修改 `application.yml` 中的端口：

```yaml
server:
  port: 8081
```

或停止占用端口的进程。

### Q: 数据库连接失败

**A**: 
1. 检查数据库配置是否正确
2. 确保数据库服务已启动
3. 检查用户名和密码
4. 检查网络连接

### Q: 启动失败：找不到主类

**A**: 
1. 确保在 `ddd-bootstrap` 模块运行
2. 主类是 `com.example.bootstrap.DddApplication`
3. 检查类路径配置

## 架构问题

### Q: 领域实体和JPA实体为什么要分离？

**A**: 
- **解耦**：领域层不依赖JPA框架
- **灵活性**：可以轻松切换持久化框架
- **清晰性**：领域模型和技术模型分离

### Q: 为什么应用服务这么薄？

**A**: 
- **单一职责**：应用服务只负责协调
- **业务逻辑在领域层**：保持领域层纯净
- **易于测试**：薄层更容易测试

### Q: 什么时候使用领域服务？

**A**: 
- 业务逻辑涉及多个实体
- 不适合放在单个实体中
- 无状态的业务逻辑

**示例**：
```java
// 跨实体的业务逻辑
public boolean canPlaceOrder(Customer customer, Order order) {
    // 检查客户信用
    // 检查库存
    // 检查其他规则
}
```

## 开发问题

### Q: 如何添加新的聚合？

**A**: 按照以下步骤：

1. **领域层**：创建实体、值对象、仓储接口
2. **应用层**：创建应用服务、DTO、命令
3. **基础设施层**：实现仓储、创建JPA实体
4. **接口层**：创建REST控制器

详细步骤见[快速开始](../guide/getting-started.md)。

### Q: 如何处理跨聚合的业务逻辑？

**A**: 
- **领域服务**：处理跨聚合的业务逻辑
- **应用服务**：协调多个聚合
- **领域事件**：通过事件解耦

**示例**：
```java
// 应用服务协调多个聚合
@Transactional
public void placeOrder(PlaceOrderCommand command) {
    Customer customer = customerRepository.findById(...);
    Order order = orderRepository.findById(...);
    
    // 使用领域服务验证
    if (!orderDomainService.canPlaceOrder(customer, order)) {
        throw new IllegalArgumentException("无法下单");
    }
    
    order.place();
    orderRepository.save(order);
    
    // 发布领域事件
    eventPublisher.publish(new OrderPlacedEvent(order.getId()));
}
```

### Q: 如何实现分页查询？

**A**: 在仓储接口中添加分页方法：

```java
// 领域层
public interface UserRepository {
    Page<User> findAll(Pageable pageable);
}

// 基础设施层
@Override
public Page<User> findAll(Pageable pageable) {
    Page<UserEntity> entities = jpaRepository.findAll(pageable);
    return entities.map(mapper::toDomain);
}
```

### Q: 如何处理软删除？

**A**: 在领域实体中添加删除标记：

```java
public class User extends BaseEntity {
    private boolean deleted;
    
    public void delete() {
        this.deleted = true;
    }
    
    public boolean isDeleted() {
        return deleted;
    }
}
```

在仓储实现中过滤已删除的记录：

```java
@Override
public Optional<User> findById(Long id) {
    return jpaRepository.findByIdAndDeletedFalse(id)
        .map(mapper::toDomain);
}
```

## 测试问题

### Q: 如何测试领域层？

**A**: 领域层测试应该快速、独立：

```java
class UserTest {
    @Test
    void shouldActivateUser() {
        User user = User.create("john", "john@example.com", "password");
        user.deactivate();
        
        user.activate();
        
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
```

### Q: 如何Mock仓储？

**A**: 使用Mockito：

```java
@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserApplicationService userApplicationService;
    
    @Test
    void shouldCreateUser() {
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        
        // 测试代码
    }
}
```

## 性能问题

### Q: 如何优化查询性能？

**A**: 
1. **使用索引**：在JPA实体上添加索引
2. **使用投影**：只查询需要的字段
3. **使用延迟加载**：避免N+1问题
4. **使用批量查询**：减少数据库往返

### Q: 如何处理N+1问题？

**A**: 
1. **使用JOIN FETCH**：
```java
@Query("SELECT o FROM OrderEntity o JOIN FETCH o.items WHERE o.id = :id")
Optional<OrderEntity> findByIdWithItems(@Param("id") Long id);
```

2. **使用EntityGraph**：
```java
@EntityGraph(attributePaths = {"items"})
Optional<OrderEntity> findById(Long id);
```

## 部署问题

### Q: 如何打包应用？

**A**: 
```bash
mvn clean package
```

打包后的JAR文件在 `ddd-bootstrap/target/ddd-bootstrap-1.0.0-SNAPSHOT.jar`

### Q: 如何运行打包后的应用？

**A**: 
```bash
java -jar ddd-bootstrap/target/ddd-bootstrap-1.0.0-SNAPSHOT.jar
```

### Q: 如何配置生产环境？

**A**: 使用Spring Profile：

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://prod-db:3306/ddd_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

运行：
```bash
java -jar app.jar --spring.profiles.active=prod
```

## 其他问题

### Q: 如何添加API文档？

**A**: 添加SpringDoc OpenAPI依赖：

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

访问：http://localhost:8080/swagger-ui.html

### Q: 如何添加日志？

**A**: Spring Boot默认使用Logback，配置 `application.yml`：

```yaml
logging:
  level:
    com.example: DEBUG
    org.springframework.web: INFO
```

### Q: 如何添加缓存？

**A**: 使用Spring Cache：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

```java
@Cacheable("users")
public UserDTO getUserById(Long id) {
    // ...
}
```

## 获取帮助

如果问题仍未解决：

1. 查看项目文档
2. 搜索GitHub Issues
3. 提交新的Issue
4. 参考Spring Boot官方文档

