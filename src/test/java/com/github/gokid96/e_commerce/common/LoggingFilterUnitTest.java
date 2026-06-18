package com.github.gokid96.e_commerce.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LoggingFilterUnitTest {

    @InjectMocks
    private LoggingFilter loggingFilter;

    @Mock
    private FilterChain filterChain;

    @DisplayName("로깅 필터가 동작하며 traceId를 응답 헤더에 담는다.")
    @Test
    void doFilterInternal() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request,10240);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // when
        loggingFilter.doFilterInternal(requestWrapper, responseWrapper, filterChain);

        // then
        assertThat(response.getHeader("X-Trace-Id")).isNotNull();
    }
}