package com.github.gokid96.e_commerce.order.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.github.gokid96.e_commerce")
@EnableJpaRepositories(basePackages = "com.github.gokid96.e_commerce")
public class JpaConfig {
}
