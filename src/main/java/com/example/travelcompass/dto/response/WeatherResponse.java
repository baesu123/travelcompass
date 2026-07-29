package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class WeatherResponse {

    // 16일 이내 실시간 예보 (OpenMeteo)
    private List<String> forecastDates;
    private List<Double> temperatureMax;
    private List<Double> temperatureMin;
    private List<Integer> precipitationProbability;

    // 16일 이후 평균 기후 정보 (정적 데이터)
    private double averageTemperature;
    private double averagePrecipitation;
    private String recommendedClothing;
    private String travelTip;

}
