package com.example.domain.model.user;

/**
 * 用户状态枚举
 * 值对象示例
 */
public enum UserStatus {
    ACTIVE("激活"),
    INACTIVE("停用"),
    LOCKED("锁定");
    
    private final String description;
    
    UserStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

