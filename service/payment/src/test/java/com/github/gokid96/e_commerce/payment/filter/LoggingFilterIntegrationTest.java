package com.github.gokid96.e_commerce.payment.filter;

import com.github.gokid96.e_commerce.payment.support.ContainerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
class LoggingFilterIntegrationTest extends ContainerTestSupport {

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

    // payment 는 HTTP 컨트롤러가 없어 ControllerAdvice 도 없다. 예외가 응답으로 변환되지 않고
    // MockMvc 밖으로 전파되므로, 예외 경로 검증은 다른 서비스의 동일 테스트에서 담당한다.

    @RestController
    static class DummyController {

        @PostMapping("/api/logging-filter-test")
        public ResponseEntity<String> test(@RequestBody String body) {
            return ResponseEntity.ok("response-body");
        }
    }
}
