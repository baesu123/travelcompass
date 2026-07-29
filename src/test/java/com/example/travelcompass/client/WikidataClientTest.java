package com.example.travelcompass.client;

import com.example.travelcompass.dto.response.WikipediaGeoSearchResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class WikidataClientTest {

    private final WikidataClient wikidataClient = new WikidataClient(WebClient.builder());

    @Test
    void 주변_관광지를_조회한다() {
        WikipediaGeoSearchResponse response = wikidataClient.getNearbyPlaces(37.5665, 126.9780)
                .block(Duration.ofSeconds(10));

        assertThat(response).isNotNull();
        assertThat(response.getQuery().getGeosearch()).isNotEmpty();
    }

}
