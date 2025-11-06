package com.app.groupdeal.global.config.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.app.groupdeal.infrastructure",
        considerNestedRepositories = true
)
public class JpaRepositoryConfig {
}
