package com.github.gokid96.e_commerce.common.message;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * 카프카 컨슈머 공통 예외 처리 설정.
 *
 * <p>리스너에서 예외가 발생하면 ack 가 수행되지 않아 동일 메시지를 무한히 재소비하게 된다.
 * 재시도 횟수를 제한하고, 한계를 넘긴 메시지는 DLT(Dead Letter Topic)로 격리해
 * 하나의 실패 메시지가 파티션 전체를 멈춰 세우지 않도록 한다.
 *
 * <p>{@code common:message} 를 의존하는 모든 서비스(coupon, order, payment, product)에
 * 자동 적용된다.
 */
@Slf4j
@Configuration
public class KafkaErrorHandlerConfig {

    /** 재시도 간격 (ms) */
    private static final long RETRY_INTERVAL_MS = 1_000L;

    /** 최초 시도 이후 추가 재시도 횟수 */
    private static final long MAX_RETRY_ATTEMPTS = 3L;

    /** DLT 토픽 접미사 */
    private static final String DLT_SUFFIX = ".DLT";

    /** 파티션 자동 선택 (원본 파티션 수와 DLT 파티션 수가 달라도 안전) */
    private static final int ANY_PARTITION = -1;

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, String> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate, (record, exception) -> {
            String deadLetterTopic = record.topic() + DLT_SUFFIX;

            log.error("카프카 메세지 처리 실패 - DLT 전송: topic={}, key={}, dlt={}",
                    record.topic(), record.key(), deadLetterTopic, exception);

            return new TopicPartition(deadLetterTopic, ANY_PARTITION);
        });
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                deadLetterPublishingRecoverer,
                new FixedBackOff(RETRY_INTERVAL_MS, MAX_RETRY_ATTEMPTS)
        );

        errorHandler.setLogLevel(KafkaException.Level.ERROR);

        return errorHandler;
    }
}
