package com.github.gokid96.e_commerce.common;

import com.github.gokid96.e_commerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(LoggingFilterIntegrationTest.DummyController.class)
class LoggingFilterIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("로깅 필터가 정상적으로 동작한다.")
    @Test
    void doFilterInternal() throws Exception {
        MvcResult result = mockMvc.perform(
                        post("/api/test")
                                .content("sample")
                                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getHeader("X-Trace-Id")).isNotBlank();
    }

    @DisplayName("예외가 발생해도 로깅 필터가 정상적으로 동작한다.")
    @Test
    void doFilterInternalWithException() throws Exception {
        MvcResult result = mockMvc.perform(
                        post("/api/test/exception")
                                .content("sample")
                                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(result.getResponse().getHeader("X-Trace-Id")).isNotBlank();
    }

    @RestController
    static class DummyController {
        @PostMapping("/api/test")
        public ResponseEntity<String> test(@RequestBody String body) {
            return ResponseEntity.ok("response-body");
        }

        @PostMapping("/api/test/exception")
        public ResponseEntity<String> testException(@RequestBody String body) {
            throw new IllegalArgumentException("test exception");
        }
    }
}