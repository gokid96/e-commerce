package com.github.gokid96.e_commerce.support;

import com.github.gokid96.e_commerce.TestcontainersConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public abstract class IntegrationTestSupport {
}