package com.github.gokid96.e_commerce.common.cache;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 캐시 모듈의 빈을 스스로 등록한다.
 * 서비스의 {@code @SpringBootApplication} 위치(컴포넌트 스캔 범위)에 의존하지 않도록
 * {@code common:client}·{@code common:message}·{@code common:outbox}와 동일하게
 * {@code AutoConfiguration.imports}로 노출한다.
 */
@Configuration
@ComponentScan(basePackages = "com.github.gokid96.e_commerce.common.cache")
public class CacheConfig {
}
