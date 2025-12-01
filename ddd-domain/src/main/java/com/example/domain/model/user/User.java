package com.example.domain.model.user;

import com.example.domain.model.BaseEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户领域实体
 * 示例领域模型
 */
@Getter
@Setter
public class User extends BaseEntity {
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotNull(message = "用户状态不能为空")
    private UserStatus status;
    
    /**
     * 创建用户
     */
    public static User create(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
    
    /**
     * 激活用户
     */
    public void activate() {
        if (this.status == UserStatus.INACTIVE) {
            this.status = UserStatus.ACTIVE;
        }
    }
    
    /**
     * 停用用户
     */
    public void deactivate() {
        if (this.status == UserStatus.ACTIVE) {
            this.status = UserStatus.INACTIVE;
        }
    }
    
    /**
     * 更新邮箱
     */
    public void updateEmail(String newEmail) {
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
        }
    }
}

