package com.interview.interview_service.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    private final HttpServletRequest request;
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate ->
        {
            String authorization = request.getHeader("Authorization");
            if (authorization != null) {
                requestTemplate.header("Authorization",
                        authorization);
            }
        };
    }
}
