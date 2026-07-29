package com.example.travelcompass.client;

import com.example.travelcompass.dto.response.OpenMeteoResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class OpenMeteoClient {

    private final WebClient webClient;

    public OpenMeteoClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.open-meteo.com/v1")
                .build();
    }

    public Mono<OpenMeteoResponse> getForecast(double latitude, double longitude) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("daily", "temperature_2m_max,temperature_2m_min,precipitation_probability_max")
                        .queryParam("timezone", "auto")
                        .queryParam("forecast_days", 16)
                        .build())
                .retrieve()
                .bodyToMono(OpenMeteoResponse.class);
    }

}
