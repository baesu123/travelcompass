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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private static final String CLIMATE_DATA_PATH = "data/climate/climate.json";

    private final RestCountriesClient restCountriesClient;
    private final OpenMeteoClient openMeteoClient;
    private final WeatherMapper weatherMapper;
    private final Map<String, ClimateInfo> climateByRegion;

    public WeatherService(RestCountriesClient restCountriesClient,
                           OpenMeteoClient openMeteoClient,
                           WeatherMapper weatherMapper,
                           ObjectMapper objectMapper) {
        this.restCountriesClient = restCountriesClient;
        this.openMeteoClient = openMeteoClient;
        this.weatherMapper = weatherMapper;
        this.climateByRegion = loadClimateData(objectMapper);
    }

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

        ClimateInfo climateInfo = climateByRegion.getOrDefault(country.getRegion(), defaultClimateInfo());

        return weatherMapper.toWeatherResponse(forecast, climateInfo);
    }

    private Map<String, ClimateInfo> loadClimateData(ObjectMapper objectMapper) {
        try (InputStream inputStream = new ClassPathResource(CLIMATE_DATA_PATH).getInputStream()) {
            List<ClimateInfo> climateInfos = objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ClimateInfo.class));

            return climateInfos.stream()
                    .collect(Collectors.toMap(ClimateInfo::getRegion, info -> info));
        } catch (IOException e) {
            throw new IllegalStateException("기후 정보 데이터를 불러올 수 없습니다.", e);
        }
    }

    /**
     * climate.json에 없는 지역(region)이 조회될 경우를 대비한 안전한 기본값.
     * 실제 평균치가 아니므로 여행자에게 "확인 필요"라는 메시지로만 안내한다.
     */
    private ClimateInfo defaultClimateInfo() {
        ClimateInfo defaultInfo = new ClimateInfo();
        defaultInfo.setRegion("Unknown");
        defaultInfo.setAverageTemperature(20.0);
        defaultInfo.setAveragePrecipitation(80.0);
        defaultInfo.setRecommendedClothing("현지 기후 정보를 확인 후 준비하세요.");
        defaultInfo.setTravelTip("여행 전 최신 기후 정보를 확인하세요.");
        return defaultInfo;
    }

}
