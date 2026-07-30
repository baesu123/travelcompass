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

/**
 * 지역(region)별 평균 기후 정보(평균 기온, 강수량, 추천 복장, 여행 팁)를 제공하는 서비스.
 * 실시간 외부 API 대신, 클래스패스에 내장된 data/climate/climate.json 파일을
 * 애플리케이션 시작 시 한 번만 읽어 메모리에 캐싱해두고 재사용한다.
 */
@Service
public class ClimateService {

    // 지역별 평균 기후 데이터가 들어있는 JSON 파일의 클래스패스 경로
    private static final String CLIMATE_DATA_PATH = "data/climate/climate.json";

    // 지역명(region) -> 기후 정보 매핑. 생성자에서 한 번만 채워지는 사실상의 캐시.
    private final Map<String, ClimateInfo> climateByRegion;

    /**
     * 스프링이 빈을 생성할 때 ObjectMapper를 주입받아, 곧바로 climate.json을 읽어
     * region -> ClimateInfo 맵으로 변환해둔다.
     * @param objectMapper JSON을 Java 객체로 변환하는 Jackson ObjectMapper
     */
    public ClimateService(ObjectMapper objectMapper) {
        this.climateByRegion = loadClimateData(objectMapper);
    }

    /**
     * 주어진 지역의 평균 기후 정보를 조회한다.
     * @param region 조회할 지역명(예: "Asia", "Europe" 등 REST Countries의 region 값)
     * @return 해당 지역의 기후 정보. 데이터에 없는 지역이면 안전한 기본값(defaultClimateInfo)을 반환
     */
    public ClimateInfo getClimateInfo(String region) {
        // getOrDefault를 사용해 매핑에 없는 region이 들어와도 NPE 없이 기본 안내 정보로 대체한다.
        return climateByRegion.getOrDefault(region, defaultClimateInfo());
    }

    /**
     * climate.json 파일을 읽어 "지역명 -> ClimateInfo" 맵으로 변환한다.
     * @param objectMapper JSON 배열을 List<ClimateInfo>로 역직렬화하기 위한 Jackson ObjectMapper
     * @return 지역별 기후 정보 매핑
     */
    private Map<String, ClimateInfo> loadClimateData(ObjectMapper objectMapper) {
        // try-with-resources로 스트림을 자동으로 닫아 리소스 누수를 방지한다.
        try (InputStream inputStream = new ClassPathResource(CLIMATE_DATA_PATH).getInputStream()) {
            List<ClimateInfo> climateInfos = objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ClimateInfo.class));

            // 리스트를 region 필드를 키로 하는 Map으로 변환해, 이후 조회를 O(1)로 만든다.
            return climateInfos.stream()
                    .collect(Collectors.toMap(ClimateInfo::getRegion, info -> info));
        } catch (IOException e) {
            // 기후 데이터 없이는 서비스가 정상 동작할 수 없으므로, 애플리케이션 구동 실패로 이어지도록
            // 비검사 예외(IllegalStateException)로 전환해서 던진다.
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
