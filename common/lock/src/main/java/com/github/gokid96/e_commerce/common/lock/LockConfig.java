package com.github.gokid96.e_commerce.common.lock;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 락 모듈의 빈을 스스로 등록한다.
 *
 * <p>이 설정이 없으면 서비스의 {@code @SpringBootApplication} 패키지 밖에 있는
 * {@code DistributedLockAspect} 가 등록되지 않아 {@code @DistributedLock} 이 조용히 무시된다.
 * {@code common:cache}·{@code common:client}·{@code common:message}·{@code common:outbox} 와
 * 동일하게 {@code AutoConfiguration.imports} 로 노출한다.
 */
@Configuration
@ComponentScan(basePackages = "com.github.gokid96.e_commerce.common.lock")
public class LockConfig {
}
