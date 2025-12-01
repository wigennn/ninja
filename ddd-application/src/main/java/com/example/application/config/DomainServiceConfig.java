package com.example.application.config;

import com.example.domain.repository.UserRepository;
import com.example.domain.service.UserDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 领域服务配置
 * 将领域服务配置为Spring Bean，保持领域层纯净
 */
@Configuration
public class DomainServiceConfig {
    
    @Bean
    public UserDomainService userDomainService(UserRepository userRepository) {
        return new UserDomainService(userRepository);
    }
}

