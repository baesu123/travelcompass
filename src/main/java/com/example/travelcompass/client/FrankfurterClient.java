package com.example.travelcompass.client;

import com.example.travelcompass.dto.response.FrankfurterResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Frankfurter(무료 환율 정보 API)를 호출해 두 통화 간 최신 환율을 가져오는 클라이언트.
 * 예산 계산기 등에서 통화 변환이 필요할 때 사용된다.
 */
@Component
public class FrankfurterClient {

    // Frankfurter API 전용으로 baseUrl이 설정된 WebClient 인스턴스
    private final WebClient webClient;

    /**
     * 공용 WebClient.Builder를 주입받아 Frankfurter API 전용 baseUrl을 지정한 WebClient를 생성한다.
     *
     * @param webClientBuilder 스프링 빈으로 등록된 공용 WebClient 빌더
     */
    public FrankfurterClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.frankfurter.dev/v1")
                .build();
    }

    /**
     * from 통화 기준으로 to 통화의 최신 환율을 조회한다. (예: from=USD, to=KRW)
     *
     * @param from 기준이 되는 원본 통화 코드 (예: "USD")
     * @param to   환산 대상 통화 코드 (예: "KRW")
     * @return 최신 환율 정보를 담은 Mono (비동기 결과)
     */
    public Mono<FrankfurterResponse> getLatestRate(String from, String to) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/latest")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .retrieve()
                .bodyToMono(FrankfurterResponse.class); // 응답 JSON을 FrankfurterResponse로 변환
    }

}
