package com.github.gokid96.e_commerce.order.support;

import com.github.gokid96.e_commerce.order.support.container.KafkaContainerExtension;
import com.github.gokid96.e_commerce.order.support.container.MySQLContainerExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;

@ExtendWith({
        MySQLContainerExtension.class,
        KafkaContainerExtension.class
})
public abstract class ContainerTestSupport {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // MySQL
        MySQLContainer<?> mySQLContainer = MySQLContainerExtension.getContainer();
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);

        // Kafka
        KafkaContainer kafkaContainer = KafkaContainerExtension.getContainer();
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }
}
