package com.example.travelcompass.client;

import com.example.travelcompass.dto.response.OpenMeteoResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Open-Meteo(무료 날씨 예보 API)를 호출해 특정 위도/경도 지점의 일별 기상 예보를 가져오는 클라이언트.
 * WebClient를 이용해 비동기(non-blocking)로 REST API를 호출한다.
 */
@Component
public class OpenMeteoClient {

    // 이 클라이언트 전용으로 baseUrl이 설정된 WebClient 인스턴스 (재사용 가능, 스레드에 안전)
    private final WebClient webClient;

    /**
     * 생성자에서 WebClientConfig가 등록해준 공용 WebClient.Builder를 주입받아
     * Open-Meteo 전용 baseUrl을 지정한 WebClient를 만들어 둔다.
     * 초보자 팁: WebClient.Builder는 매번 새로 build()해서 써야 설정이 서로 섞이지 않는다.
     *
     * @param webClientBuilder 스프링 빈으로 등록된 공용 WebClient 빌더 (WebClientConfig 참고)
     */
    public OpenMeteoClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.open-meteo.com/v1")
                .build();
    }

    /**
     * 지정한 위도/경도 위치의 16일치 일별 최고/최저 기온과 강수 확률 예보를 조회한다.
     * 초보자 팁: 이 메서드는 block() 없이 Mono(비동기 결과 컨테이너)를 그대로 반환하므로,
     * 호출한 쪽(서비스 계층)에서 필요에 따라 block()으로 동기 변환하거나 리액티브하게 조합해서 쓴다.
     *
     * @param latitude  조회할 지점의 위도
     * @param longitude 조회할 지점의 경도
     * @return 일별 기온/강수확률 예보 데이터를 담은 Mono (구독 시점에 실제 HTTP 요청이 발생)
     */
    public Mono<OpenMeteoResponse> getForecast(double latitude, double longitude) {
        return webClient.get()
                // uriBuilder로 쿼리 파라미터를 조립해 최종 요청 URL을 만든다
                .uri(uriBuilder -> uriBuilder
                        .path("/forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        // daily: 하루 단위로 받고 싶은 지표 목록 (최고/최저기온, 강수확률)
                        .queryParam("daily", "temperature_2m_max,temperature_2m_min,precipitation_probability_max")
                        // timezone=auto: 위경도 기준으로 현지 시간대를 자동 적용
                        .queryParam("timezone", "auto")
                        // forecast_days: 앞으로 며칠치 예보를 받을지 (최대 16일)
                        .queryParam("forecast_days", 16)
                        .build())
                .retrieve() // 응답을 받아오되, 4xx/5xx면 기본적으로 예외(WebClientResponseException)를 던짐
                .bodyToMono(OpenMeteoResponse.class); // JSON 응답 바디를 OpenMeteoResponse 객체로 역직렬화
    }

}
