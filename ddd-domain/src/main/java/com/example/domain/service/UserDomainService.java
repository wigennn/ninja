package com.example.domain.service;

import com.example.domain.model.user.User;
import com.example.domain.repository.UserRepository;

/**
 * 用户领域服务
 * 处理跨实体的业务逻辑
 */
public class UserDomainService {
    
    private final UserRepository userRepository;
    
    public UserDomainService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return 是否可用
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    /**
     * 检查邮箱是否可用
     * @param email 邮箱
     * @return 是否可用
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    
    /**
     * 验证用户是否可以注册
     * @param username 用户名
     * @param email 邮箱
     * @return 是否可以注册
     */
    public boolean canRegister(String username, String email) {
        return isUsernameAvailable(username) && isEmailAvailable(email);
    }
}

