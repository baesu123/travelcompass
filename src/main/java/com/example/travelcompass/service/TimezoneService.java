package com.example.travelcompass.service;

import com.example.travelcompass.common.exception.BusinessException;
import com.example.travelcompass.common.exception.ErrorCode;
import com.example.travelcompass.dto.response.TimezoneResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 국가별 시차(현지 시간 vs 한국 시간) 정보를 제공하는 서비스.
 * 외부 API를 호출하지 않고, 클래스패스에 내장된 data/timezones.json 파일을
 * 애플리케이션 시작 시 한 번만 읽어 메모리에 올려두고 재사용한다. (빠르고 외부 의존성이 없음)
 */
@Service
public class TimezoneService {

    // 시차 계산의 기준이 되는 한국 시간대. 상수로 고정해두어 매번 새로 생성하지 않는다.
    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    // 국가별 시간대 데이터가 들어있는 JSON 파일의 클래스패스 경로 (src/main/resources 기준)
    private static final String TIMEZONE_DATA_PATH = "data/timezones.json";
    // 화면에 보여줄 날짜/시간 문자열 포맷 (예: 2026-07-30 15:00:00)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 국가 코드 -> ZoneId 문자열 매핑을 담아두는 캐시 성격의 맵. 생성자에서 한 번만 채워진다.
    private final Map<String, String> timezoneByCountryCode;

    /**
     * 스프링이 빈을 생성할 때 ObjectMapper(JSON 파서)를 주입받아,
     * 곧바로 timezones.json 파일을 읽어 메모리 맵으로 변환해둔다.
     * @param objectMapper JSON 데이터를 Java 객체로 변환하는 Jackson의 핵심 클래스
     */
    public TimezoneService(ObjectMapper objectMapper) {
        this.timezoneByCountryCode = loadTimezones(objectMapper);
    }

    /**
     * 주어진 국가 코드의 현재 현지 시간과 한국과의 시차를 계산해서 반환한다.
     * @param countryCode 조회할 국가 코드 (대소문자 무관, 내부적으로 대문자로 변환)
     * @return 현지 시간/한국 시간/시차(시간 단위)가 담긴 응답 DTO
     */
    public TimezoneResponse getTimezone(String countryCode) {
        String zoneIdText = timezoneByCountryCode.get(countryCode.toUpperCase());
        if (zoneIdText == null) {
            // 매핑 데이터에 없는 국가 코드는 잘못된 입력으로 간주하고 비즈니스 예외를 던진다.
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "시간대 정보를 찾을 수 없는 국가 코드입니다: " + countryCode);
        }

        ZoneId targetZone = ZoneId.of(zoneIdText);
        // 동일한 시각(now)을 기준으로 두 시간대로 각각 변환해야 정확한 시차가 나온다.
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime koreaNow = now.withZoneSameInstant(KOREA_ZONE);
        ZonedDateTime targetNow = now.withZoneSameInstant(targetZone);

        // 시차(시간) = (대상국 UTC 오프셋 - 한국 UTC 오프셋) / 3600초.
        // 서머타임(DST) 적용 국가도 getOffset()이 그 시점의 실제 오프셋을 반환하므로 자동으로 반영된다.
        double timeDifferenceHours =
                (targetNow.getOffset().getTotalSeconds() - koreaNow.getOffset().getTotalSeconds()) / 3600.0;

        return TimezoneResponse.builder()
                .countryCode(countryCode.toUpperCase())
                .timezone(zoneIdText)
                .localTime(targetNow.format(FORMATTER))
                .koreaTime(koreaNow.format(FORMATTER))
                .timeDifferenceHours(timeDifferenceHours)
                .build();
    }

    /**
     * 클래스패스의 timezones.json 파일을 읽어 "국가코드 -> ZoneId 문자열" 맵으로 변환한다.
     * @param objectMapper JSON을 Map으로 역직렬화하기 위한 Jackson ObjectMapper
     * @return 국가 코드별 시간대 매핑 정보
     */
    private Map<String, String> loadTimezones(ObjectMapper objectMapper) {
        // try-with-resources로 InputStream을 사용 후 자동으로 닫아 리소스 누수를 방지한다.
        try (InputStream inputStream = new ClassPathResource(TIMEZONE_DATA_PATH).getInputStream()) {
            return objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
        } catch (IOException e) {
            // 파일을 못 읽으면 서비스 자체가 정상 동작할 수 없으므로, 애플리케이션 구동을 막는
            // IllegalStateException(비검사 예외)으로 전환해 즉시 문제를 드러낸다.
            throw new IllegalStateException("시간대 데이터를 불러올 수 없습니다.", e);
        }
    }

}
