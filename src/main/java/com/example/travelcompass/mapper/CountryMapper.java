package com.example.travelcompass.mapper;

import com.example.travelcompass.dto.response.CountryDetailResponse;
import com.example.travelcompass.dto.response.FrankfurterResponse;
import com.example.travelcompass.dto.response.OpenMeteoResponse;
import com.example.travelcompass.dto.response.RestCountryResponse;
import com.example.travelcompass.dto.response.WikipediaGeoSearchResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CountryMapper {

    public CountryDetailResponse toCountryDetailResponse(RestCountryResponse country,
                                                           WikipediaGeoSearchResponse wiki,
                                                           FrankfurterResponse exchange,
                                                           OpenMeteoResponse weather,
                                                           boolean favorite,
                                                           String memberNickname) {

        OpenMeteoResponse.Daily daily = weather.getDaily();

        return CountryDetailResponse.builder()
                .countryCode(country.getCca2())
                .commonName(country.getName().getCommon())
                .officialName(country.getName().getOfficial())
                .capital(country.getCapital())
                .region(country.getRegion())
                .subregion(country.getSubregion())
                .currencies(country.getCurrencies())
                .languages(country.getLanguages())
                .latlng(country.getLatlng())
                .flags(country.getFlags())
                .attractions(toAttractions(wiki))
                .currencyCode(exchange.getBase())
                .exchangeRateToKrw(findExchangeRate(exchange))
                .forecastDates(daily != null ? daily.getTime() : Collections.emptyList())
                .temperatureMax(daily != null ? daily.getTemperature2mMax() : Collections.emptyList())
                .temperatureMin(daily != null ? daily.getTemperature2mMin() : Collections.emptyList())
                .precipitationProbability(daily != null ? daily.getPrecipitationProbabilityMax() : Collections.emptyList())
                .favorite(favorite)
                .memberNickname(memberNickname)
                .build();
    }

    private List<CountryDetailResponse.AttractionInfo> toAttractions(WikipediaGeoSearchResponse wiki) {
        if (wiki.getQuery() == null || wiki.getQuery().getGeosearch() == null) {
            return Collections.emptyList();
        }

        return wiki.getQuery().getGeosearch().stream()
                .map(item -> CountryDetailResponse.AttractionInfo.builder()
                        .title(item.getTitle())
                        .lat(item.getLat())
                        .lon(item.getLon())
                        .distanceMeters(item.getDist())
                        .build())
                .collect(Collectors.toList());
    }

    private Double findExchangeRate(FrankfurterResponse exchange) {
        if (exchange.getRates() == null || exchange.getRates().isEmpty()) {
            return null;
        }
        return exchange.getRates().values().stream().findFirst().orElse(null);
    }

}
