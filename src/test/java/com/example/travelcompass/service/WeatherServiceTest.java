package com.example.travelcompass.service;

import com.example.travelcompass.client.OpenMeteoClient;
import com.example.travelcompass.client.RestCountriesClient;
import com.example.travelcompass.dto.response.WeatherResponse;
import com.example.travelcompass.mapper.WeatherMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class WeatherServiceTest {

    private final WeatherService weatherService = new WeatherService(
            new RestCountriesClient(new ObjectMapper()),
            new OpenMeteoClient(WebClient.builder()),
            new WeatherMapper(),
            new ObjectMapper()
    );

    @Test
    void 국가코드로_실시간_예보와_평균_기후정보를_함께_조회한다() {
        WeatherResponse response = weatherService.getWeather("kr");

        assertThat(response.getForecastDates()).isNotEmpty();
        assertThat(response.getTemperatureMax()).isNotEmpty();
        assertThat(response.getPrecipitationProbability()).isNotEmpty();

        assertThat(response.getAverageTemperature()).isEqualTo(21.0);
        assertThat(response.getRecommendedClothing()).isNotBlank();
        assertThat(response.getTravelTip()).isNotBlank();
    }

}
