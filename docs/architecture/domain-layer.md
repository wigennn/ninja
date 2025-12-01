# 领域层设计

领域层是DDD架构的核心，包含业务领域的所有核心概念和业务规则。

## 目录结构

```
ddd-domain/
└── src/main/java/com/example/domain/
    ├── model/              # 领域模型
    │   ├── BaseEntity.java
    │   ├── ValueObject.java
    │   └── user/          # 用户聚合
    │       ├── User.java
    │       └── UserStatus.java
    ├── repository/         # 仓储接口
    │   └── UserRepository.java
    └── service/            # 领域服务
        └── UserDomainService.java
```

## 核心组件

### 实体（Entity）

实体是具有唯一标识的对象，通过ID区分。

**示例：User实体**

```java
public class User extends BaseEntity {
    private String username;
    private String email;
    private String password;
    private UserStatus status;
    
    // 领域行为
    public void activate() { ... }
    public void deactivate() { ... }
    public void updateEmail(String newEmail) { ... }
}
```

**设计要点**：
- 实体包含业务行为，不仅仅是数据容器
- 通过方法封装业务规则
- 保持实体的不变性约束

### 值对象（Value Object）

值对象通过值相等性比较，不可变。

**示例：UserStatus枚举**

```java
public enum UserStatus {
    ACTIVE("激活"),
    INACTIVE("停用"),
    LOCKED("锁定");
}
```

**设计要点**：
- 值对象应该是不可变的
- 通过值相等性比较
- 可以包含验证逻辑

### 领域服务（Domain Service）

领域服务处理跨实体的业务逻辑。

**示例：UserDomainService**

```java
public class UserDomainService {
    public boolean canRegister(String username, String email) {
        return isUsernameAvailable(username) 
            && isEmailAvailable(email);
    }
}
```

**使用场景**：
- 跨多个实体的业务逻辑
- 复杂的业务规则验证
- 不适合放在单个实体中的逻辑

### 仓储接口（Repository Interface）

仓储接口定义数据访问契约，实现在基础设施层。

**示例：UserRepository**

```java
public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    // ...
}
```

**设计要点**：
- 接口定义在领域层
- 使用领域对象作为参数和返回值
- 实现放在基础设施层

## 设计原则

### 1. 富领域模型（Rich Domain Model）

实体应该包含业务行为，而不仅仅是getter/setter。

**好的设计**：
```java
public class User {
    public void activate() {
        if (this.status == UserStatus.INACTIVE) {
            this.status = UserStatus.ACTIVE;
        }
    }
}
```

**不好的设计**：
```java
public class User {
    // 业务逻辑在应用层
    public void setStatus(UserStatus status) { ... }
}
```

### 2. 聚合根（Aggregate Root）

聚合根是聚合的入口，外部只能通过聚合根访问聚合内的实体。

**设计要点**：
- 每个聚合只有一个聚合根
- 聚合根负责维护聚合内的不变性约束
- 通过ID引用其他聚合

### 3. 不变性约束（Invariant）

聚合必须始终保持业务规则的一致性。

**示例**：
```java
public class User {
    public void updateEmail(String newEmail) {
        if (newEmail == null || newEmail.equals(this.email)) {
            return; // 不满足条件，不更新
        }
        this.email = newEmail;
    }
}
```

## 最佳实践

1. **保持领域层纯净**：不依赖任何外部框架
2. **使用领域语言**：类名、方法名使用业务术语
3. **封装业务规则**：业务规则封装在实体或领域服务中
4. **避免贫血模型**：实体应该包含行为，不只是数据
5. **明确聚合边界**：清晰定义聚合的范围

## 示例：用户聚合

完整的用户聚合示例：

```
User (聚合根)
├── UserStatus (值对象)
├── UserRepository (仓储接口)
└── UserDomainService (领域服务)
```

这个聚合封装了用户相关的所有业务逻辑和数据。

