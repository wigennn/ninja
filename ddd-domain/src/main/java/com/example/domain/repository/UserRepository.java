package com.example.domain.repository;

import com.example.domain.model.user.User;

import java.util.Optional;

/**
 * 用户仓储接口
 * 定义在领域层，实现在基础设施层
 */
public interface UserRepository {
    
    /**
     * 保存用户
     * @param user 用户实体
     * @return 保存后的用户
     */
    User save(User user);
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户实体
     */
    Optional<User> findById(Long id);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户实体
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户实体
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteById(Long id);
    
    /**
     * 判断用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 判断邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 查询所有用户
     * @return 用户列表
     */
    java.util.List<User> findAll();
}

