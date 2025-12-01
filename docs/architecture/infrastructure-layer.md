# 基础设施层设计

基础设施层提供技术实现，包括持久化、外部服务集成等技术细节。

## 目录结构

```
ddd-infrastructure/
└── src/main/java/com/example/infrastructure/
    ├── persistence/
    │   ├── entity/           # JPA实体
    │   │   └── UserEntity.java
    │   ├── repository/       # 仓储实现
    │   │   ├── UserJpaRepository.java
    │   │   └── UserRepositoryImpl.java
    │   └── mapper/           # 实体映射器
    │       └── UserEntityMapper.java
    └── config/               # 配置类
        └── JpaConfig.java
```

## 核心组件

### JPA实体（Persistence Entity）

JPA实体映射到数据库表，与领域实体分离。

**示例：UserEntity**

```java
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    // ...
}
```

**设计要点**：
- 与领域实体分离
- 只包含持久化相关的注解
- 不包含业务逻辑

### 仓储实现（Repository Implementation）

实现领域层定义的仓储接口。

**示例：UserRepositoryImpl**

```java
@Repository
public class UserRepositoryImpl implements UserRepository {
    
    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userEntityMapper;
    
    @Override
    public User save(User user) {
        UserEntity entity = userEntityMapper.toEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return userEntityMapper.toDomain(savedEntity);
    }
}
```

**职责**：
- 实现领域层的仓储接口
- 在领域实体和JPA实体之间转换
- 调用Spring Data JPA完成持久化

### 实体映射器（Entity Mapper）

在领域实体和JPA实体之间转换。

**示例：UserEntityMapper**

```java
@Component
public class UserEntityMapper {
    
    public UserEntity toEntity(User user) {
        return UserEntity.builder()
            .id(user.getId())
            .username(user.getUsername())
            // ...
            .build();
    }
    
    public User toDomain(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        // ...
        return user;
    }
}
```

**设计要点**：
- 处理领域对象和持久化对象的差异
- 处理枚举等类型的转换
- 保持转换逻辑集中

### Spring Data JPA Repository

使用Spring Data JPA简化数据访问。

**示例：UserJpaRepository**

```java
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

## 设计模式

### 适配器模式（Adapter Pattern）

基础设施层作为适配器，将技术实现适配到领域接口：

```
领域接口 (UserRepository)
    ↑
适配器 (UserRepositoryImpl)
    ↓
技术实现 (UserJpaRepository)
```

### 数据映射模式（Data Mapper Pattern）

使用映射器在领域对象和持久化对象之间转换：

```
领域对象 (User) ←→ 映射器 ←→ 持久化对象 (UserEntity)
```

## 配置

### JPA配置

```java
@Configuration
@EnableJpaRepositories(basePackages = "com.example.infrastructure.persistence.repository")
@EntityScan(basePackages = "com.example.infrastructure.persistence.entity")
public class JpaConfig {
}
```

### 数据源配置

在`application.yml`中配置：

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: update
```

## 最佳实践

### 1. 分离领域实体和持久化实体

领域实体和JPA实体应该分离：

```java
// 领域实体（ddd-domain）
public class User extends BaseEntity { ... }

// JPA实体（ddd-infrastructure）
@Entity
public class UserEntity { ... }
```

**好处**：
- 领域层不依赖JPA
- 可以灵活改变持久化策略
- 领域模型和技术模型解耦

### 2. 使用映射器转换

不要直接在领域实体上使用JPA注解：

```java
// 好的设计
UserEntity entity = mapper.toEntity(user);
User user = mapper.toDomain(entity);

// 不好的设计
@Entity
public class User { // 领域实体不应该有JPA注解
    @Id
    private Long id;
}
```

### 3. 处理时间戳

在仓储实现中自动处理创建时间和更新时间：

```java
public User save(User user) {
    UserEntity entity = mapper.toEntity(user);
    if (user.getId() == null) {
        entity.setCreatedAt(LocalDateTime.now());
    }
    entity.setUpdatedAt(LocalDateTime.now());
    // ...
}
```

### 4. 处理枚举转换

在映射器中处理枚举和字符串的转换：

```java
public UserEntity toEntity(User user) {
    return UserEntity.builder()
        .status(user.getStatus().name()) // 枚举转字符串
        .build();
}

public User toDomain(UserEntity entity) {
    User user = new User();
    user.setStatus(UserStatus.valueOf(entity.getStatus())); // 字符串转枚举
    return user;
}
```

## 扩展点

### 添加新的持久化实现

如果需要切换数据库或ORM框架：

1. 实现领域层的仓储接口
2. 创建新的持久化实体
3. 创建映射器
4. 更新配置

### 集成外部服务

在基础设施层添加外部服务适配器：

```java
@Component
public class EmailServiceAdapter implements EmailService {
    // 实现外部邮件服务调用
}
```

## 示例：完整的仓储实现

```java
@Repository
public class UserRepositoryImpl implements UserRepository {
    
    private final UserJpaRepository jpaRepository;
    private final UserEntityMapper mapper;
    
    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        if (user.getId() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        return mapper.toDomain(jpaRepository.save(entity));
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }
    
    // 其他方法...
}
```

