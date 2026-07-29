package com.example.travelcompass.client;

import com.example.travelcompass.dto.response.FrankfurterResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class FrankfurterClientTest {

    private final FrankfurterClient frankfurterClient = new FrankfurterClient(WebClient.builder());

    @Test
    void 환율을_조회한다() {
        FrankfurterResponse response = frankfurterClient.getLatestRate("USD", "KRW")
                .block(Duration.ofSeconds(10));

        assertThat(response).isNotNull();
        assertThat(response.getRates()).containsKey("KRW");
    }

}
