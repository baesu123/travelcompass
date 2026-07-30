package com.example.travelcompass.service;

import com.example.travelcompass.client.FrankfurterClient;
import com.example.travelcompass.client.OpenMeteoClient;
import com.example.travelcompass.client.RestCountriesClient;
import com.example.travelcompass.client.WikidataClient;
import com.example.travelcompass.dto.response.CountryDetailResponse;
import com.example.travelcompass.mapper.CountryMapper;
import com.example.travelcompass.mapper.FavoriteMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CountryFacadeServiceTest {

    private final FavoriteMapper favoriteMapper = mock(FavoriteMapper.class);

    private final CountryFacadeService countryFacadeService = new CountryFacadeService(
            new RestCountriesClient(new ObjectMapper()),
            new WikidataClient(WebClient.builder()),
            new FrankfurterClient(WebClient.builder()),
            new OpenMeteoClient(WebClient.builder()),
            favoriteMapper,
            new CountryMapper(),
            new ClimateService(new ObjectMapper())
    );

    @Test
    void 국가코드로_국가상세정보를_조립한다() {
        CountryDetailResponse response = countryFacadeService.getCountryDetail("kr", null, null);

        assertThat(response).isNotNull();
        assertThat(response.getCommonName()).isEqualTo("South Korea");
        assertThat(response.getCurrencyCode()).isEqualTo("KRW");
        assertThat(response.getExchangeRateToKrw()).isEqualTo(1.0);
        assertThat(response.getAttractions()).isNotEmpty();
        assertThat(response.getForecastDates()).isNotEmpty();
        assertThat(response.isFavorite()).isFalse();
    }

    @Test
    void 외화_환율이_필요한_국가도_조립한다() {
        CountryDetailResponse response = countryFacadeService.getCountryDetail("jp", null, null);

        assertThat(response).isNotNull();
        assertThat(response.getCommonName()).isEqualTo("Japan");
        assertThat(response.getCurrencyCode()).isEqualTo("JPY");
        assertThat(response.getExchangeRateToKrw()).isNotNull();
        assertThat(response.getExchangeRateToKrw()).isNotEqualTo(1.0);
    }

}
