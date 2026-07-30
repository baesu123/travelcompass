package com.example.travelcompass.service;

import com.example.travelcompass.dto.response.ClimateInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClimateService {

    private static final String CLIMATE_DATA_PATH = "data/climate/climate.json";

    private final Map<String, ClimateInfo> climateByRegion;

    public ClimateService(ObjectMapper objectMapper) {
        this.climateByRegion = loadClimateData(objectMapper);
    }

    public ClimateInfo getClimateInfo(String region) {
        return climateByRegion.getOrDefault(region, defaultClimateInfo());
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
