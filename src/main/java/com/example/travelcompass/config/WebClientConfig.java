package com.example.travelcompass.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// 외부 API 호출(client 패키지)에서 공통으로 사용할 WebClient.Builder를 빈으로 등록
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

}
