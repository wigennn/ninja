package com.example.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA配置
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.example.infrastructure.persistence.repository")
@EntityScan(basePackages = "com.example.infrastructure.persistence.entity")
public class JpaConfig {
}

