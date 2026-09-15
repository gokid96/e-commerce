package com.github.gokid96.e_commerce.outbox;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = {
        "com.github.gokid96.e_commerce.outbox",
        "com.github.gokid96.e_commerce.common.outbox"
})
public class OutboxConfig {
}
