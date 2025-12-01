# 接口层设计

接口层提供外部访问入口，处理HTTP请求、参数验证、异常处理等。

## 目录结构

```
ddd-interfaces/
└── src/main/java/com/example/interfaces/
    └── rest/
        ├── controller/      # REST控制器
        │   └── UserController.java
        ├── dto/            # 请求/响应DTO
        │   ├── CreateUserRequest.java
        │   ├── UpdateUserRequest.java
        │   └── UserResponse.java
        ├── mapper/         # REST映射器
        │   └── UserRestMapper.java
        └── exception/      # 异常处理
            ├── GlobalExceptionHandler.java
            └── ErrorResponse.java
```

## 核心组件

### REST控制器（Controller）

处理HTTP请求，调用应用服务。

**示例：UserController**

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserApplicationService userApplicationService;
    private final UserRestMapper userRestMapper;
    
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        CreateUserCommand command = userRestMapper.toCreateCommand(request);
        UserDTO userDTO = userApplicationService.createUser(command);
        UserResponse response = userRestMapper.toResponse(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

**职责**：
- 接收HTTP请求
- 参数验证
- 调用应用服务
- 返回HTTP响应

**不包含**：
- 业务逻辑
- 数据访问逻辑

### 请求DTO（Request DTO）

封装HTTP请求参数。

**示例：CreateUserRequest**

```java
public class CreateUserRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}
```

**设计要点**：
- 使用验证注解
- 只包含必要的字段
- 与领域对象分离

### 响应DTO（Response DTO）

封装HTTP响应数据。

**示例：UserResponse**

```java
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**设计要点**：
- 不包含敏感信息（如密码）
- 只包含需要返回的字段
- 使用Builder模式（可选）

### 全局异常处理器

统一处理异常，返回标准错误响应。

**示例：GlobalExceptionHandler**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e) {
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(e.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e) {
        // 处理验证错误
    }
}
```

## RESTful API设计

### URL设计

```
GET    /api/users          # 查询所有用户
GET    /api/users/{id}     # 查询单个用户
POST   /api/users          # 创建用户
PUT    /api/users/{id}     # 更新用户
DELETE /api/users/{id}     # 删除用户
POST   /api/users/{id}/activate   # 激活用户
POST   /api/users/{id}/deactivate # 停用用户
```

### HTTP状态码

- `200 OK`：成功
- `201 Created`：创建成功
- `204 No Content`：删除成功
- `400 Bad Request`：请求错误
- `404 Not Found`：资源不存在
- `500 Internal Server Error`：服务器错误

### 响应格式

**成功响应**：
```json
{
  "id": 1,
  "username": "john",
  "email": "john@example.com",
  "status": "ACTIVE",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

**错误响应**：
```json
{
  "status": 400,
  "message": "用户名或邮箱已存在",
  "errors": {
    "email": "邮箱格式不正确"
  }
}
```

## 参数验证

### 使用Bean Validation

```java
@PostMapping
public ResponseEntity<UserResponse> createUser(
        @Valid @RequestBody CreateUserRequest request) {
    // 验证会自动执行
}
```

### 验证注解

```java
public class CreateUserRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Size(min = 8, message = "密码长度至少8位")
    private String password;
}
```

## 最佳实践

### 1. 保持控制器薄

控制器应该很薄，只负责HTTP相关处理：

```java
// 好的设计
@PostMapping
public ResponseEntity<UserResponse> createUser(
        @Valid @RequestBody CreateUserRequest request) {
    CreateUserCommand command = mapper.toCreateCommand(request);
    UserDTO dto = applicationService.createUser(command);
    return ResponseEntity.ok(mapper.toResponse(dto));
}

// 不好的设计
@PostMapping
public ResponseEntity<UserResponse> createUser(
        @Valid @RequestBody CreateUserRequest request) {
    // 业务逻辑不应该在这里
    if (userRepository.existsByUsername(request.getUsername())) {
        throw ...; // 应该在应用层
    }
    // ...
}
```

### 2. 使用DTO转换

使用MapStruct进行DTO转换：

```java
CreateUserCommand command = userRestMapper.toCreateCommand(request);
UserResponse response = userRestMapper.toResponse(userDTO);
```

### 3. 统一异常处理

使用`@RestControllerAdvice`统一处理异常：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 处理各种异常
}
```

### 4. 返回适当的HTTP状态码

```java
@PostMapping
public ResponseEntity<UserResponse> createUser(...) {
    // ...
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    // ...
    return ResponseEntity.noContent().build();
}
```

### 5. 使用路径变量和查询参数

```java
@GetMapping("/{id}")
public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
    // ...
}

@GetMapping
public ResponseEntity<List<UserResponse>> getUsers(
        @RequestParam(required = false) String status) {
    // ...
}
```

## 安全考虑

### 1. 输入验证

所有输入都应该验证：

```java
@Valid @RequestBody CreateUserRequest request
```

### 2. 敏感信息过滤

响应中不包含敏感信息：

```java
// UserResponse中不包含password字段
public class UserResponse {
    // 不包含password
}
```

### 3. 错误信息

错误信息不应该泄露系统内部信息：

```java
// 好的设计
throw new IllegalArgumentException("用户不存在");

// 不好的设计
throw new IllegalArgumentException("User with id " + id + " not found in database");
```

## 示例：完整的控制器

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserApplicationService userApplicationService;
    private final UserRestMapper userRestMapper;
    
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        CreateUserCommand command = userRestMapper.toCreateCommand(request);
        UserDTO userDTO = userApplicationService.createUser(command);
        UserResponse response = userRestMapper.toResponse(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userApplicationService.getUserById(id);
        UserResponse response = userRestMapper.toResponse(userDTO);
        return ResponseEntity.ok(response);
    }
    
    // 其他方法...
}
```

