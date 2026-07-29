package com.example.travelcompass.mapper;

import com.example.travelcompass.dto.response.ClimateInfo;
import com.example.travelcompass.dto.response.OpenMeteoResponse;
import com.example.travelcompass.dto.response.WeatherResponse;
import org.springframework.stereotype.Component;

@Component
public class WeatherMapper {

    public WeatherResponse toWeatherResponse(OpenMeteoResponse forecast, ClimateInfo climateInfo) {
        OpenMeteoResponse.Daily daily = forecast.getDaily();

        return WeatherResponse.builder()
                .forecastDates(daily.getTime())
                .temperatureMax(daily.getTemperature2mMax())
                .temperatureMin(daily.getTemperature2mMin())
                .precipitationProbability(daily.getPrecipitationProbabilityMax())
                .averageTemperature(climateInfo.getAverageTemperature())
                .averagePrecipitation(climateInfo.getAveragePrecipitation())
                .recommendedClothing(climateInfo.getRecommendedClothing())
                .travelTip(climateInfo.getTravelTip())
                .build();
    }

}
