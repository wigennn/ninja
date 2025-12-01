package com.example.infrastructure.persistence.mapper;

import com.example.domain.model.user.User;
import com.example.domain.model.user.UserStatus;
import com.example.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * 用户实体映射器
 * 在领域实体和持久化实体之间转换
 */
@Component
public class UserEntityMapper {
    
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        
        return UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .build();
    }
    
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        user.setStatus(UserStatus.valueOf(entity.getStatus()));
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());
        user.setCreatedBy(entity.getCreatedBy());
        user.setUpdatedBy(entity.getUpdatedBy());
        
        return user;
    }
}

