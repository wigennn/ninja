# DDD核心概念

本文档介绍领域驱动设计（DDD）的核心概念。

## 什么是DDD

领域驱动设计（Domain-Driven Design，DDD）是一种软件开发方法论，强调：

- **以领域为核心**：业务逻辑应该在领域层，而不是技术层
- **通用语言（Ubiquitous Language）**：开发团队和业务专家使用相同的术语
- **分层架构**：清晰的层次划分，每层职责明确

## 核心概念

### 实体（Entity）

实体是具有唯一标识的对象，通过ID区分。

**特点**：
- 有唯一标识（ID）
- 生命周期可追踪
- 通过ID相等性比较

**示例**：
```java
public class User extends BaseEntity {
    private Long id;  // 唯一标识
    private String username;
    private String email;
}
```

### 值对象（Value Object）

值对象通过值相等性比较，不可变。

**特点**：
- 没有唯一标识
- 不可变
- 通过值相等性比较

**示例**：
```java
public class Email {
    private final String value;
    
    public Email(String value) {
        this.value = validate(value);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }
}
```

### 聚合（Aggregate）

聚合是一组相关对象的集合，作为一个整体来维护数据一致性。

**特点**：
- 有一个聚合根（Aggregate Root）
- 通过聚合根访问聚合内的实体
- 维护聚合内的不变性约束

**示例**：
```java
// Order是聚合根
public class Order extends BaseEntity {
    private List<OrderItem> items; // 聚合内的实体
    
    public void addItem(OrderItem item) {
        // 维护不变性约束
        if (this.status != OrderStatus.DRAFT) {
            throw new IllegalStateException("只能向草稿订单添加商品");
        }
        this.items.add(item);
    }
}
```

### 聚合根（Aggregate Root）

聚合根是聚合的入口，外部只能通过聚合根访问聚合。

**特点**：
- 每个聚合只有一个聚合根
- 通过ID引用其他聚合
- 负责维护聚合内的不变性约束

### 仓储（Repository）

仓储封装了获取对象引用所需的逻辑，提供类似集合的接口。

**特点**：
- 接口定义在领域层
- 实现在基础设施层
- 使用领域对象作为参数和返回值

**示例**：
```java
// 领域层：接口
public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
}

// 基础设施层：实现
@Repository
public class UserRepositoryImpl implements UserRepository {
    // 实现细节
}
```

### 领域服务（Domain Service）

领域服务处理跨实体的业务逻辑。

**使用场景**：
- 业务逻辑涉及多个实体
- 不适合放在单个实体中
- 无状态的业务逻辑

**示例**：
```java
public class UserDomainService {
    public boolean canRegister(String username, String email) {
        return !userRepository.existsByUsername(username)
            && !userRepository.existsByEmail(email);
    }
}
```

### 应用服务（Application Service）

应用服务协调领域对象完成用例。

**职责**：
- 协调领域对象
- 管理事务边界
- 对象转换（领域对象 ↔ DTO）

**不包含**：
- 业务逻辑（应该在领域层）
- 技术细节（应该在基础设施层）

### 领域事件（Domain Event）

领域事件表示领域中发生的重要事情。

**特点**：
- 表示已发生的事情
- 不可变
- 包含发生时间

**示例**：
```java
public class OrderPlacedEvent {
    private final Long orderId;
    private final Long customerId;
    private final LocalDateTime occurredAt;
    
    public OrderPlacedEvent(Long orderId, Long customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.occurredAt = LocalDateTime.now();
    }
}
```

## 分层架构

### 领域层（Domain Layer）

**职责**：
- 包含业务逻辑
- 定义实体、值对象、领域服务
- 定义仓储接口

**特点**：
- 不依赖任何外部框架
- 是系统的核心

### 应用层（Application Layer）

**职责**：
- 协调领域对象完成用例
- 管理事务
- 对象转换

**特点**：
- 薄层，不包含业务逻辑
- 依赖领域层

### 基础设施层（Infrastructure Layer）

**职责**：
- 实现仓储
- 提供技术实现
- 集成外部服务

**特点**：
- 实现技术细节
- 可替换的实现

### 接口层（Interfaces Layer）

**职责**：
- 处理HTTP请求
- 参数验证
- 异常处理

**特点**：
- 处理外部交互
- 依赖应用层

## 设计原则

### 1. 依赖倒置原则（DIP）

- 高层模块不依赖低层模块，都依赖抽象
- 领域层定义接口，基础设施层实现

### 2. 单一职责原则（SRP）

- 每个类只有一个职责
- 每层职责明确

### 3. 开闭原则（OCP）

- 对扩展开放，对修改关闭
- 通过接口和抽象类实现

### 4. 里氏替换原则（LSP）

- 子类可以替换父类
- 接口实现可以互相替换

## 通用语言（Ubiquitous Language）

通用语言是开发团队和业务专家共同使用的语言。

**示例**：
- 业务术语：订单（Order）、客户（Customer）
- 业务操作：下单（Place Order）、取消订单（Cancel Order）
- 业务规则：订单金额不能为负

**好处**：
- 减少沟通成本
- 代码更易理解
- 业务逻辑更清晰

## 有界上下文（Bounded Context）

有界上下文是模型的边界，在这个边界内，所有术语和概念都有特定的含义。

**特点**：
- 每个有界上下文有自己的领域模型
- 不同上下文之间通过接口通信
- 避免模型污染

## 战略设计

### 上下文映射（Context Mapping）

描述不同有界上下文之间的关系：

- **共享内核（Shared Kernel）**：共享部分模型
- **客户-供应商（Customer-Supplier）**：上游为下游服务
- **遵奉者（Conformist）**：下游完全依赖上游
- **防腐层（Anti-Corruption Layer）**：隔离外部系统
- **独立方式（Separate Ways）**：完全独立

## 战术设计

### 实体 vs 值对象

**选择实体**：
- 需要唯一标识
- 需要追踪生命周期
- 需要修改状态

**选择值对象**：
- 通过值相等性比较
- 不可变
- 可以替换

### 聚合设计

**设计原则**：
- 保持聚合小
- 通过ID引用其他聚合
- 一个事务只修改一个聚合

## 总结

DDD的核心是：

1. **以领域为核心**：业务逻辑在领域层
2. **通用语言**：使用业务术语
3. **分层架构**：清晰的层次划分
4. **聚合设计**：合理划分聚合边界
5. **仓储模式**：封装数据访问

通过遵循这些原则，可以构建出易于理解、易于维护、易于扩展的软件系统。

