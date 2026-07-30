package com.example.travelcompass.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * client 패키지의 각 API 클라이언트(OpenMeteoClient, WikidataClient, FrankfurterClient 등)가
 * 공통으로 주입받아 사용할 WebClient.Builder를 스프링 빈으로 등록하는 설정 클래스.
 * 초보자 팁: WebClient.Builder를 빈으로 하나 등록해두고, 각 클라이언트에서 baseUrl()만 다르게 지정해
 * build()하면 매번 새로운 설정(예: 인증 헤더, 타임아웃 등 추가 시 공통 적용)을 재사용할 수 있다.
 */
// 외부 API 호출(client 패키지)에서 공통으로 사용할 WebClient.Builder를 빈으로 등록
@Configuration
public class WebClientConfig {

    /**
     * 아직 baseUrl 등이 지정되지 않은 기본 WebClient.Builder를 빈으로 등록한다.
     * 이 빌더는 프로토타입처럼 각 클라이언트 생성자에서 개별적으로 baseUrl()을 지정해 build()된다.
     *
     * @return 기본 설정의 WebClient.Builder
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

}
