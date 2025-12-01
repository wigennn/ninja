# 应用层设计

应用层负责协调领域对象完成业务用例，是连接接口层和领域层的桥梁。

## 目录结构

```
ddd-application/
└── src/main/java/com/example/application/
    ├── dto/              # 数据传输对象
    │   └── UserDTO.java
    ├── command/          # 命令对象
    │   ├── CreateUserCommand.java
    │   └── UpdateUserCommand.java
    ├── service/          # 应用服务
    │   └── UserApplicationService.java
    └── mapper/           # 对象映射器
        └── UserMapper.java
```

## 核心组件

### 应用服务（Application Service）

应用服务编排领域对象完成用例。

**示例：UserApplicationService**

```java
@Service
@Transactional
public class UserApplicationService {
    
    public UserDTO createUser(CreateUserCommand command) {
        // 1. 领域服务验证
        if (!userDomainService.canRegister(...)) {
            throw new IllegalArgumentException(...);
        }
        
        // 2. 创建领域对象
        User user = User.create(...);
        
        // 3. 保存
        User savedUser = userRepository.save(user);
        
        // 4. 转换为DTO
        return userMapper.toDTO(savedUser);
    }
}
```

**职责**：
- 协调领域对象完成用例
- 管理事务边界
- 处理应用级别的验证
- 对象转换（领域对象 ↔ DTO）

**不包含**：
- 业务逻辑（应该在领域层）
- 技术细节（应该在基础设施层）

### 命令（Command）

命令对象封装写操作的输入参数。

**示例：CreateUserCommand**

```java
public class CreateUserCommand {
    @NotBlank
    private String username;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String password;
}
```

**设计要点**：
- 使用验证注解
- 只包含必要的字段
- 不可变（推荐）

### DTO（Data Transfer Object）

DTO用于在不同层之间传输数据。

**示例：UserDTO**

```java
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String status;
    private LocalDateTime createdAt;
}
```

**设计要点**：
- 只包含数据，不包含行为
- 使用MapStruct进行转换
- 与领域对象分离

### 对象映射器（Mapper）

使用MapStruct进行对象转换。

**示例：UserMapper**

```java
@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    @Mapping(target = "status", expression = "java(user.getStatus().name())")
    UserDTO toDTO(User user);
}
```

## 设计模式

### CQRS（命令查询职责分离）

虽然本项目未完全实现CQRS，但采用了类似的思路：

- **命令（Command）**：写操作
- **查询（Query）**：读操作（可以单独定义Query对象）

### 事务管理

应用服务方法使用`@Transactional`管理事务：

```java
@Transactional
public UserDTO createUser(CreateUserCommand command) {
    // 事务边界
}
```

**读操作**使用`@Transactional(readOnly = true)`：

```java
@Transactional(readOnly = true)
public UserDTO getUserById(Long id) {
    // 只读事务
}
```

## 最佳实践

### 1. 保持应用服务薄

应用服务应该很薄，只负责协调：

```java
// 好的设计
public UserDTO createUser(CreateUserCommand command) {
    // 验证
    if (!canRegister(...)) throw ...;
    
    // 创建
    User user = User.create(...);
    
    // 保存
    return userMapper.toDTO(userRepository.save(user));
}

// 不好的设计
public UserDTO createUser(CreateUserCommand command) {
    // 业务逻辑不应该在这里
    if (command.getPassword().length() < 8) {
        throw ...; // 应该在领域层
    }
    // ...
}
```

### 2. 使用领域服务验证

复杂的业务验证应该使用领域服务：

```java
if (!userDomainService.canRegister(username, email)) {
    throw new IllegalArgumentException("用户名或邮箱已存在");
}
```

### 3. 异常处理

应用层应该抛出业务异常，由接口层统一处理：

```java
User user = userRepository.findById(id)
    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
```

### 4. 对象转换

使用MapStruct进行对象转换，避免手动转换：

```java
UserDTO dto = userMapper.toDTO(user);
```

## 工作流程

典型的应用服务工作流程：

```
1. 接收命令/查询
   ↓
2. 参数验证（使用验证注解）
   ↓
3. 业务验证（使用领域服务）
   ↓
4. 调用领域对象完成业务逻辑
   ↓
5. 保存到仓储
   ↓
6. 转换为DTO返回
```

## 示例：完整的用例

创建用户的完整流程：

```java
@Transactional
public UserDTO createUser(CreateUserCommand command) {
    // 1. 业务验证
    if (!userDomainService.canRegister(
            command.getUsername(), 
            command.getEmail())) {
        throw new IllegalArgumentException("用户名或邮箱已存在");
    }
    
    // 2. 创建领域对象
    User user = User.create(
        command.getUsername(),
        command.getEmail(),
        command.getPassword()
    );
    
    // 3. 持久化
    User savedUser = userRepository.save(user);
    
    // 4. 返回DTO
    return userMapper.toDTO(savedUser);
}
```

