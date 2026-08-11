package com.github.gokid96.e_commerce.common.serialize;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataSerializer {

    private static final ObjectMapper MAPPER = initialize();

    private static ObjectMapper initialize() {
        return JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // 역직렬화 시 없는 필드 무시
                .build();
    }

    public static <T> T deserialize(String data, Class<T> type) {
        try {
            return MAPPER.readValue(data, type);
        } catch (Exception e) {
            log.error("[DataSerializer.deserialize] JSON 역직렬화 실패: data={} type={}", data, type, e);
            return null;
        }
    }

    public static <T> T deserialize(Object data, Class<T> type) {
        return MAPPER.convertValue(data, type);
    }

    public static String serialize(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            log.error("[DataSerializer.serialize] JSON 직렬화 실패: object={}", object, e);
            return null;
        }
    }
}