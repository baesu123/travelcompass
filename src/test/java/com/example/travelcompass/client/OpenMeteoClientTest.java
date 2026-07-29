package com.example.travelcompass.client;

import com.example.travelcompass.dto.response.OpenMeteoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class OpenMeteoClientTest {

    private final OpenMeteoClient openMeteoClient = new OpenMeteoClient(WebClient.builder());

    @Test
    void 날씨예보를_조회한다() {
        OpenMeteoResponse response = openMeteoClient.getForecast(37.5665, 126.9780)
                .block(Duration.ofSeconds(10));

        assertThat(response).isNotNull();
        assertThat(response.getDaily().getTime()).isNotEmpty();
    }

}
