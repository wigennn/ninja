# 开发规范

本文档定义了项目的开发规范和编码标准。

## 代码规范

### 命名规范

#### 包命名

- 使用小写字母
- 使用点分隔
- 遵循分层结构

```
com.example.domain.model.user
com.example.application.service
com.example.infrastructure.persistence.repository
```

#### 类命名

- 使用大驼峰（PascalCase）
- 类名应该清晰表达意图

```
User, UserRepository, UserApplicationService
CreateUserCommand, UserDTO, UserResponse
```

#### 方法命名

- 使用小驼峰（camelCase）
- 方法名应该是动词或动词短语

```
createUser, updateUser, deleteUser
findById, findByUsername, existsByEmail
```

#### 变量命名

- 使用小驼峰（camelCase）
- 变量名应该清晰表达含义

```
User user = new User();
UserRepository userRepository;
```

### 代码组织

#### 包结构

每层都有清晰的包结构：

**领域层**：
```
com.example.domain
├── model          # 领域模型
├── repository     # 仓储接口
└── service        # 领域服务
```

**应用层**：
```
com.example.application
├── dto            # 数据传输对象
├── command        # 命令对象
├── service        # 应用服务
└── mapper         # 对象映射器
```

**基础设施层**：
```
com.example.infrastructure
├── persistence
│   ├── entity     # JPA实体
│   ├── repository # 仓储实现
│   └── mapper     # 实体映射器
└── config         # 配置类
```

**接口层**：
```
com.example.interfaces
└── rest
    ├── controller # REST控制器
    ├── dto        # 请求/响应DTO
    ├── mapper     # REST映射器
    └── exception  # 异常处理
```

### 注释规范

#### 类注释

每个类都应该有JavaDoc注释：

```java
/**
 * 用户领域实体
 * 示例领域模型
 */
public class User extends BaseEntity {
    // ...
}
```

#### 方法注释

公共方法应该有JavaDoc注释：

```java
/**
 * 创建用户
 * @param command 创建用户命令
 * @return 用户DTO
 */
public UserDTO createUser(CreateUserCommand command) {
    // ...
}
```

#### 复杂逻辑注释

对于复杂的业务逻辑，应该添加注释说明：

```java
// 领域服务验证：检查用户名和邮箱是否可用
if (!userDomainService.canRegister(username, email)) {
    throw new IllegalArgumentException("用户名或邮箱已存在");
}
```

## 设计规范

### 领域层规范

1. **实体应该包含行为**
   ```java
   // 好的设计
   public void activate() {
       this.status = UserStatus.ACTIVE;
   }
   
   // 不好的设计
   public void setStatus(UserStatus status) {
       this.status = status;
   }
   ```

2. **使用领域语言**
   ```java
   // 好的设计
   user.activate();
   user.deactivate();
   
   // 不好的设计
   user.setStatus(UserStatus.ACTIVE);
   ```

3. **保持领域层纯净**
   - 不依赖任何外部框架
   - 不包含技术注解

### 应用层规范

1. **保持应用服务薄**
   ```java
   // 好的设计：只协调
   public UserDTO createUser(CreateUserCommand command) {
       if (!canRegister(...)) throw ...;
       User user = User.create(...);
       return mapper.toDTO(repository.save(user));
   }
   
   // 不好的设计：包含业务逻辑
   public UserDTO createUser(CreateUserCommand command) {
       if (command.getPassword().length() < 8) {
           // 业务逻辑应该在领域层
       }
   }
   ```

2. **使用命令对象**
   ```java
   // 好的设计
   public UserDTO createUser(CreateUserCommand command)
   
   // 不好的设计
   public UserDTO createUser(String username, String email, String password)
   ```

### 基础设施层规范

1. **分离领域实体和JPA实体**
   ```java
   // 领域实体（ddd-domain）
   public class User extends BaseEntity
   
   // JPA实体（ddd-infrastructure）
   @Entity
   public class UserEntity
   ```

2. **使用映射器转换**
   ```java
   // 好的设计
   UserEntity entity = mapper.toEntity(user);
   User user = mapper.toDomain(entity);
   
   // 不好的设计
   // 直接在领域实体上使用JPA注解
   ```

### 接口层规范

1. **保持控制器薄**
   ```java
   // 好的设计
   @PostMapping
   public ResponseEntity<UserResponse> createUser(
           @Valid @RequestBody CreateUserRequest request) {
       CreateUserCommand command = mapper.toCreateCommand(request);
       UserDTO dto = applicationService.createUser(command);
       return ResponseEntity.ok(mapper.toResponse(dto));
   }
   ```

2. **使用验证注解**
   ```java
   @PostMapping
   public ResponseEntity<UserResponse> createUser(
           @Valid @RequestBody CreateUserRequest request) {
       // 验证会自动执行
   }
   ```

## 异常处理规范

### 异常类型

1. **业务异常**：使用`IllegalArgumentException`
   ```java
   throw new IllegalArgumentException("用户不存在");
   ```

2. **验证异常**：使用Bean Validation
   ```java
   @NotBlank(message = "用户名不能为空")
   private String username;
   ```

### 异常处理

1. **应用层抛出业务异常**
   ```java
   User user = repository.findById(id)
       .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
   ```

2. **接口层统一处理异常**
   ```java
   @RestControllerAdvice
   public class GlobalExceptionHandler {
       @ExceptionHandler(IllegalArgumentException.class)
       public ResponseEntity<ErrorResponse> handle(...) {
           // 统一处理
       }
   }
   ```

## 测试规范

### 单元测试

每个类都应该有单元测试：

```java
@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserApplicationService userApplicationService;
    
    @Test
    void shouldCreateUser() {
        // Given
        CreateUserCommand command = new CreateUserCommand(...);
        
        // When
        UserDTO result = userApplicationService.createUser(command);
        
        // Then
        assertThat(result).isNotNull();
    }
}
```

### 集成测试

关键流程应该有集成测试：

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldCreateUser() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"john\",...}"))
            .andExpect(status().isCreated());
    }
}
```

## Git提交规范

### 提交信息格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

**类型（type）**：
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档
- `style`: 格式
- `refactor`: 重构
- `test`: 测试
- `chore`: 构建/工具

**示例**：
```
feat(user): 添加用户激活功能

实现用户激活和停用功能，包括：
- 添加activate和deactivate方法
- 更新用户状态枚举
- 添加相应的REST端点
```

## 代码审查清单

提交代码前检查：

- [ ] 代码符合命名规范
- [ ] 有适当的注释
- [ ] 遵循分层架构原则
- [ ] 有单元测试
- [ ] 通过所有测试
- [ ] 没有编译警告
- [ ] 代码格式化
- [ ] 提交信息清晰

