package com.example.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * DDD应用启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.example.domain",
        "com.example.application",
        "com.example.infrastructure",
        "com.example.interfaces",
        "com.example.bootstrap"
})
public class DddApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DddApplication.class, args);
    }
}

