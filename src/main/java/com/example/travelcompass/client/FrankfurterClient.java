package com.example.travelcompass.client;

import com.example.travelcompass.dto.response.FrankfurterResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class FrankfurterClient {

    private final WebClient webClient;

    public FrankfurterClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.frankfurter.dev/v1")
                .build();
    }

    public Mono<FrankfurterResponse> getLatestRate(String from, String to) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/latest")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .retrieve()
                .bodyToMono(FrankfurterResponse.class);
    }

}
