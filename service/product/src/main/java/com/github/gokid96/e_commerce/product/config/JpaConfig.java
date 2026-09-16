package com.github.gokid96.e_commerce.product.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * {@code ProductApplication}이 서비스 패키지({@code ..e_commerce.product})에 있으므로
 * 스캔 기본 범위에는 {@code common:outbox}의 엔티티·리포지토리가 포함되지 않는다.
 * order·payment와 동일하게 루트 패키지를 명시해 아웃박스 스키마를 인식시킨다.
 */
@Configuration
@EntityScan(basePackages = "com.github.gokid96.e_commerce")
@EnableJpaRepositories(basePackages = "com.github.gokid96.e_commerce")
public class JpaConfig {
}
