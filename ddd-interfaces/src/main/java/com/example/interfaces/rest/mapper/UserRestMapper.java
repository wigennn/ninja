package com.example.interfaces.rest.mapper;

import com.example.application.command.CreateUserCommand;
import com.example.application.dto.UserDTO;
import com.example.interfaces.rest.dto.CreateUserRequest;
import com.example.interfaces.rest.dto.UserResponse;
import org.mapstruct.Mapper;

/**
 * REST层对象映射器
 */
@Mapper(componentModel = "spring")
public interface UserRestMapper {
    
    CreateUserCommand toCreateCommand(CreateUserRequest request);
    
    UserResponse toResponse(UserDTO dto);
}

