package com.example.travelcompass.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 날씨 예보 정보를 제공하는 외부 API인 Open-Meteo API의 응답 JSON을 매핑하기 위한 DTO.
 * Jackson이 JSON 응답 필드를 이 클래스의 필드와 매칭시켜 자동으로 역직렬화(deserialize)한다.
 *
 * Lombok 사용 팁:
 * - @Getter / @Setter : Jackson이 역직렬화할 때 setter로 값을 채우고, 이후 서비스 로직에서
 *   getter로 값을 꺼내 쓰기 위해 두 어노테이션을 함께 사용한다.
 */
@Getter
@Setter
public class OpenMeteoResponse {

    // 조회한 위치의 위도
    private double latitude;
    // 조회한 위치의 경도
    private double longitude;
    // 해당 위치의 타임존 (예: "Asia/Seoul")
    private String timezone;
    // 일별(daily) 예보 데이터를 담는 중첩 객체
    private Daily daily;

    /**
     * Open-Meteo 응답 중 "daily" 항목을 매핑하는 중첩 클래스.
     * 각 리스트는 인덱스가 같은 날짜(time)에 대응하는 값들의 배열이다.
     * (예: time[0]="2026-07-30" 이면 temperature2mMax[0]이 그 날의 최고기온)
     */
    @Getter
    @Setter
    public static class Daily {

        // 예보 날짜 목록 (예: ["2026-07-30", "2026-07-31", ...])
        private List<String> time;

        // JSON의 "temperature_2m_max" 필드를 자바 카멜케이스 필드명으로 매핑.
        // 지상 2m 기준 일 최고 기온 목록.
        @JsonProperty("temperature_2m_max")
        private List<Double> temperature2mMax;

        // JSON의 "temperature_2m_min" 필드를 자바 카멜케이스 필드명으로 매핑.
        // 지상 2m 기준 일 최저 기온 목록.
        @JsonProperty("temperature_2m_min")
        private List<Double> temperature2mMin;

        // JSON의 "precipitation_probability_max" 필드를 자바 카멜케이스 필드명으로 매핑.
        // 일별 최대 강수 확률(%) 목록.
        @JsonProperty("precipitation_probability_max")
        private List<Integer> precipitationProbabilityMax;

    }

}
