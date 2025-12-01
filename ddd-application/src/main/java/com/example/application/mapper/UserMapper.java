package com.example.application.mapper;

import com.example.application.dto.UserDTO;
import com.example.domain.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户对象映射器
 * 使用MapStruct进行对象转换
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "status", expression = "java(user.getStatus().name())")
    UserDTO toDTO(User user);
}

