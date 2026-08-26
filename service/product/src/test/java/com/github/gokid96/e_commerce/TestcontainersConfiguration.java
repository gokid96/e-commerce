package com.github.gokid96.e_commerce;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    public MySQLContainer<?> mySQLContainer() {
        return new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));
    }
}