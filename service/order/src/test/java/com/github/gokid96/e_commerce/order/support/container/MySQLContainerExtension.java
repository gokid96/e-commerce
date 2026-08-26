package com.github.gokid96.e_commerce.order.support.container;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class MySQLContainerExtension implements BeforeAllCallback {

    private static final MySQLContainer<?> MYSQL_CONTAINER;

    static {
        MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
                .withDatabaseName("ecommerce")
                .withUsername("test")
                .withPassword("test");
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        if (MYSQL_CONTAINER.isRunning()) {
            return;
        }

        MYSQL_CONTAINER.start();
    }

    public static MySQLContainer<?> getContainer() {
        return MYSQL_CONTAINER;
    }
}
