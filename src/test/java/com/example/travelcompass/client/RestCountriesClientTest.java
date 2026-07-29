package com.example.travelcompass.client;

import com.example.travelcompass.dto.response.RestCountryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class RestCountriesClientTest {

    private final RestCountriesClient restCountriesClient = new RestCountriesClient(new ObjectMapper());

    @Test
    void 국가코드로_국가정보를_조회한다() {
        RestCountryResponse response = restCountriesClient.getCountryByCode("kr")
                .block(Duration.ofSeconds(3));

        assertThat(response).isNotNull();
        assertThat(response.getName().getCommon()).isEqualTo("South Korea");
        assertThat(response.getFlags().getPng()).contains("flagcdn.com");
    }

}
