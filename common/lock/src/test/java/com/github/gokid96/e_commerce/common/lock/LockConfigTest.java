package com.github.gokid96.e_commerce.common.lock;

import com.github.gokid96.e_commerce.common.lock.infrastructure.PubSubLockTemplate;
import com.github.gokid96.e_commerce.common.lock.infrastructure.SpinLockTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * 락 모듈이 서비스의 컴포넌트 스캔 범위와 무관하게 등록되는지 확인한다.
 * 이 설정이 빠지면 {@code @DistributedLock} 이 예외 없이 무시되므로 테스트로 고정한다.
 */
class LockConfigTest {

    @SuppressWarnings("unchecked")
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(LockConfig.class))
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
            .withBean("redisTemplate", RedisTemplate.class, () -> mock(StringRedisTemplate.class));

    @DisplayName("락 모듈을 의존하면 아스펙트와 전략 빈이 자동으로 등록된다.")
    @Test
    void registersLockBeans() {
        contextRunner.run(context -> assertThat(context)
                .hasSingleBean(DistributedLockAspect.class)
                .hasSingleBean(LockStrategyRegistry.class)
                .hasSingleBean(LockKeyGenerator.class)
                .hasSingleBean(LockIdHolder.class)
                .hasSingleBean(SpinLockTemplate.class)
                .hasSingleBean(PubSubLockTemplate.class));
    }

    @DisplayName("등록된 전략으로 락 템플릿을 조회할 수 있다.")
    @Test
    void resolvesEachStrategy() {
        contextRunner.run(context -> {
            LockStrategyRegistry registry = context.getBean(LockStrategyRegistry.class);

            assertThat(registry.getLockTemplate(LockStrategy.SPIN_LOCK)).isInstanceOf(SpinLockTemplate.class);
            assertThat(registry.getLockTemplate(LockStrategy.PUB_SUB_LOCK)).isInstanceOf(PubSubLockTemplate.class);
        });
    }
}
