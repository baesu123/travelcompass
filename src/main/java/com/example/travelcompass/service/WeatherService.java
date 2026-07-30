package com.example.travelcompass.service;

import com.example.travelcompass.client.OpenMeteoClient;
import com.example.travelcompass.client.RestCountriesClient;
import com.example.travelcompass.common.exception.BusinessException;
import com.example.travelcompass.common.exception.ErrorCode;
import com.example.travelcompass.dto.response.ClimateInfo;
import com.example.travelcompass.dto.response.OpenMeteoResponse;
import com.example.travelcompass.dto.response.RestCountryResponse;
import com.example.travelcompass.dto.response.WeatherResponse;
import com.example.travelcompass.mapper.WeatherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestCountriesClient restCountriesClient;
    private final OpenMeteoClient openMeteoClient;
    private final WeatherMapper weatherMapper;
    private final ClimateService climateService;

    public WeatherResponse getWeather(String countryCode) {
        RestCountryResponse country = restCountriesClient.getCountryByCode(countryCode)
                .block(Duration.ofSeconds(5));

        if (country == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "존재하지 않는 국가 코드입니다: " + countryCode);
        }

        double lat = country.getLatlng().get(0);
        double lon = country.getLatlng().get(1);

        OpenMeteoResponse forecast = openMeteoClient.getForecast(lat, lon)
                .block(Duration.ofSeconds(5));

        ClimateInfo climateInfo = climateService.getClimateInfo(country.getRegion());

        return weatherMapper.toWeatherResponse(forecast, climateInfo);
    }

}
