package com.github.gokid96.e_commerce.user.filter;

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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoggingFilterUnitTest {

    @InjectMocks
    private LoggingFilter loggingFilter;

    @Mock
    private FilterChain filterChain;

    @DisplayName("요청을 통과시키고 추적 ID를 응답 헤더에 담는다.")
    @Test
    void doFilterInternal() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/test");
        MockHttpServletResponse response = new MockHttpServletResponse();

        loggingFilter.doFilterInternal(
                new ContentCachingRequestWrapper(request, 10240),
                new ContentCachingResponseWrapper(response),
                filterChain);

        assertThat(response.getHeader("X-Trace-Id")).isNotBlank();
        verify(filterChain).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
