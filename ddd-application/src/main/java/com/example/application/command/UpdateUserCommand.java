package com.example.application.command;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新用户命令
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserCommand {
    
    private Long id;
    
    @Email(message = "邮箱格式不正确")
    private String email;
}

