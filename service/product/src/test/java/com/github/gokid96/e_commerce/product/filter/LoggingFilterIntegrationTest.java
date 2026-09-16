package com.github.gokid96.e_commerce.product.filter;

import com.github.gokid96.e_commerce.product.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Import(LoggingFilterIntegrationTest.DummyController.class)
class LoggingFilterIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("필터를 거쳐도 응답 본문이 그대로 전달되고 추적 ID가 담긴다.")
    @Test
    void doFilterInternal() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/logging-filter-test")
                        .content("request-body")
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andReturn();

        // copyBodyToResponse 가 빠지면 상태만 200 이고 본문이 비어 나간다.
        assertThat(result.getResponse().getContentAsString()).isEqualTo("response-body");
        assertThat(result.getResponse().getHeader("X-Trace-Id")).isNotBlank();
    }

    @DisplayName("예외가 발생해도 추적 ID가 담긴다.")
    @Test
    void doFilterInternalWithException() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/logging-filter-test/exception")
                        .content("request-body")
                        .contentType(MediaType.TEXT_PLAIN))
                .andReturn();


        // 예외 처리 방식은 서비스마다 다르므로(payment 는 ControllerAdvice 가 없다) 오류 응답 여부만 확인한다.
        assertThat(result.getResponse().getStatus()).isGreaterThanOrEqualTo(400);
        assertThat(result.getResponse().getHeader("X-Trace-Id")).isNotBlank();
    }

    @RestController
    static class DummyController {

        @PostMapping("/api/logging-filter-test")
        public ResponseEntity<String> test(@RequestBody String body) {
            return ResponseEntity.ok("response-body");
        }

        @PostMapping("/api/logging-filter-test/exception")
        public ResponseEntity<String> testException(@RequestBody String body) {
            throw new IllegalArgumentException("logging filter test");
        }
    }
}
