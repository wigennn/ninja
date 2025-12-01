package com.example.interfaces.rest.controller;

import com.example.application.command.CreateUserCommand;
import com.example.application.command.UpdateUserCommand;
import com.example.application.dto.UserDTO;
import com.example.application.service.UserApplicationService;
import com.example.interfaces.rest.dto.CreateUserRequest;
import com.example.interfaces.rest.dto.UpdateUserRequest;
import com.example.interfaces.rest.dto.UserResponse;
import com.example.interfaces.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户REST控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserApplicationService userApplicationService;
    private final UserRestMapper userRestMapper;
    
    public UserController(
            UserApplicationService userApplicationService,
            UserRestMapper userRestMapper) {
        this.userApplicationService = userApplicationService;
        this.userRestMapper = userRestMapper;
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserCommand command = userRestMapper.toCreateCommand(request);
        UserDTO userDTO = userApplicationService.createUser(command);
        UserResponse response = userRestMapper.toResponse(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UpdateUserCommand command = new UpdateUserCommand(id, request.getEmail());
        UserDTO userDTO = userApplicationService.updateUser(command);
        UserResponse response = userRestMapper.toResponse(userDTO);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userApplicationService.getUserById(id);
        UserResponse response = userRestMapper.toResponse(userDTO);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 查询所有用户
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserDTO> userDTOs = userApplicationService.getAllUsers();
        List<UserResponse> responses = userDTOs.stream()
                .map(userRestMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userApplicationService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 激活用户
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        UserDTO userDTO = userApplicationService.activateUser(id);
        UserResponse response = userRestMapper.toResponse(userDTO);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 停用用户
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        UserDTO userDTO = userApplicationService.deactivateUser(id);
        UserResponse response = userRestMapper.toResponse(userDTO);
        return ResponseEntity.ok(response);
    }
}

