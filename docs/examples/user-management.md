# 用户管理示例

本文档通过用户管理功能展示完整的DDD实现。

## 功能概述

用户管理功能包括：
- 创建用户
- 查询用户
- 更新用户
- 删除用户
- 激活/停用用户

## 领域模型

### User实体

```java
public class User extends BaseEntity {
    private String username;
    private String email;
    private String password;
    private UserStatus status;
    
    public static User create(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
    
    public void activate() {
        if (this.status == UserStatus.INACTIVE) {
            this.status = UserStatus.ACTIVE;
        }
    }
    
    public void deactivate() {
        if (this.status == UserStatus.ACTIVE) {
            this.status = UserStatus.INACTIVE;
        }
    }
}
```

## 完整流程

### 创建用户

1. **接口层**：接收HTTP请求
2. **应用层**：验证并创建用户
3. **领域层**：执行业务逻辑
4. **基础设施层**：持久化到数据库

### API示例

**创建用户**：
```bash
POST /api/users
{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}
```

**查询用户**：
```bash
GET /api/users/1
```

**更新用户**：
```bash
PUT /api/users/1
{
  "email": "newemail@example.com"
}
```

