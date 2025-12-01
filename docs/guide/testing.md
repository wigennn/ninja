# 测试指南

本文档介绍如何编写测试。

## 测试类型

### 单元测试
测试单个类或方法。

### 集成测试
测试多个组件协作。

## 测试示例

### 领域层测试
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

### 应用服务测试
```java
@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserApplicationService userApplicationService;
    
    @Test
    void shouldCreateUser() {
        // 测试代码
    }
}
```

