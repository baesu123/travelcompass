package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class CountryDetailResponse {

    // 국가 기본 정보 (RestCountries)
    private String countryCode;
    private String commonName;
    private String officialName;
    private List<String> capital;
    private String region;
    private String subregion;
    private Map<String, RestCountryResponse.Currency> currencies;
    private Map<String, String> languages;
    private List<Double> latlng;
    private RestCountryResponse.Flags flags;

    // 주요 관광지 (Wiki)
    private List<AttractionInfo> attractions;

    // 환율 (현지통화 1단위 = 원화)
    private String currencyCode;
    private Double exchangeRateToKrw;

    // 날씨 (16일 예보)
    private List<String> forecastDates;
    private List<Double> temperatureMax;
    private List<Double> temperatureMin;
    private List<Integer> precipitationProbability;

    // 로그인/즐겨찾기 정보
    private boolean favorite;
    private String memberNickname;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AttractionInfo {
        private String title;
        private double lat;
        private double lon;
        private double distanceMeters;
    }

}
