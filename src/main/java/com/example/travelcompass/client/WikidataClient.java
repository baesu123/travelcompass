package com.example.travelcompass.client;

import com.example.travelcompass.dto.response.WikipediaGeoSearchResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WikidataClient {

    private final WebClient webClient;

    public WikidataClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://en.wikipedia.org/w/api.php")
                .build();
    }

    public Mono<WikipediaGeoSearchResponse> getNearbyPlaces(double latitude, double longitude) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("action", "query")
                        .queryParam("list", "geosearch")
                        .queryParam("gscoord", latitude + "|" + longitude)
                        .queryParam("gsradius", 10000)
                        .queryParam("gslimit", 10)
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .bodyToMono(WikipediaGeoSearchResponse.class);
    }

}
