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

@Service
public class TimezoneService {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final String TIMEZONE_DATA_PATH = "data/timezones.json";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Map<String, String> timezoneByCountryCode;

    public TimezoneService(ObjectMapper objectMapper) {
        this.timezoneByCountryCode = loadTimezones(objectMapper);
    }

    public TimezoneResponse getTimezone(String countryCode) {
        String zoneIdText = timezoneByCountryCode.get(countryCode.toUpperCase());
        if (zoneIdText == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "시간대 정보를 찾을 수 없는 국가 코드입니다: " + countryCode);
        }

        ZoneId targetZone = ZoneId.of(zoneIdText);
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime koreaNow = now.withZoneSameInstant(KOREA_ZONE);
        ZonedDateTime targetNow = now.withZoneSameInstant(targetZone);

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

    private Map<String, String> loadTimezones(ObjectMapper objectMapper) {
        try (InputStream inputStream = new ClassPathResource(TIMEZONE_DATA_PATH).getInputStream()) {
            return objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
        } catch (IOException e) {
            throw new IllegalStateException("시간대 데이터를 불러올 수 없습니다.", e);
        }
    }

}
