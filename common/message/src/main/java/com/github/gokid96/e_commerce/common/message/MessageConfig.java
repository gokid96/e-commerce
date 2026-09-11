package com.github.gokid96.e_commerce.common.message;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@ComponentScan(basePackages = {
        "com.github.gokid96.e_commerce.common.message",
        "com.github.gokid96.e_commerce.common.infrastructure"
})
public class MessageConfig {
}