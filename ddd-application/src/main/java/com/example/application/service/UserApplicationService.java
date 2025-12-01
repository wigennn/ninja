package com.example.application.service;

import com.example.application.command.CreateUserCommand;
import com.example.application.command.UpdateUserCommand;
import com.example.application.dto.UserDTO;
import com.example.application.mapper.UserMapper;
import com.example.domain.model.user.User;
import com.example.domain.repository.UserRepository;
import com.example.domain.service.UserDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户应用服务
 * 协调领域对象完成用例
 */
@Service
@Transactional
public class UserApplicationService {
    
    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final UserMapper userMapper;
    
    public UserApplicationService(
            UserRepository userRepository,
            UserDomainService userDomainService,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userDomainService = userDomainService;
        this.userMapper = userMapper;
    }
    
    /**
     * 创建用户
     */
    public UserDTO createUser(CreateUserCommand command) {
        // 领域服务验证
        if (!userDomainService.canRegister(command.getUsername(), command.getEmail())) {
            throw new IllegalArgumentException("用户名或邮箱已存在");
        }
        
        // 创建领域对象
        User user = User.create(
                command.getUsername(),
                command.getEmail(),
                command.getPassword()
        );
        
        // 保存
        User savedUser = userRepository.save(user);
        
        // 转换为DTO
        return userMapper.toDTO(savedUser);
    }
    
    /**
     * 更新用户
     */
    public UserDTO updateUser(UpdateUserCommand command) {
        User user = userRepository.findById(command.getId())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        if (command.getEmail() != null) {
            if (!userDomainService.isEmailAvailable(command.getEmail())) {
                throw new IllegalArgumentException("邮箱已存在");
            }
            user.updateEmail(command.getEmail());
        }
        
        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }
    
    /**
     * 根据ID查询用户
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return userMapper.toDTO(user);
    }
    
    /**
     * 查询所有用户
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        // 这里简化处理，实际应该使用分页查询
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        if (!userRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }
        userRepository.deleteById(id);
    }
    
    /**
     * 激活用户
     */
    public UserDTO activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.activate();
        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }
    
    /**
     * 停用用户
     */
    public UserDTO deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.deactivate();
        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }
}

