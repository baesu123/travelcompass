package com.example.travelcompass.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 국가 기본 정보를 제공하는 외부 API인 RestCountries API의 응답 JSON을 매핑하기 위한 DTO.
 * (프로젝트 정책상 현재는 RestCountries API가 키 인증을 요구하도록 바뀌어, 실제로는
 * 정적 countries.json 데이터를 이 구조로 매핑해 사용 중이다.)
 * Jackson이 JSON 응답 필드를 이 클래스의 필드와 매칭시켜 자동으로 역직렬화(deserialize)한다.
 *
 * Lombok 사용 팁:
 * - @Getter / @Setter : Jackson이 역직렬화 시 setter로 값을 채우고, 서비스 로직에서
 *   getter로 값을 꺼내 쓰기 위해 함께 사용한다.
 */
@Getter
@Setter
public class RestCountryResponse {

    // 국가 코드 (ISO 3166-1 alpha-2, 2자리, 예: "KR")
    private String cca2;
    // 국가 코드 (ISO 3166-1 alpha-3, 3자리, 예: "KOR")
    private String cca3;
    // 국가 이름 정보(공식명/일반명)를 담는 중첩 객체
    private Name name;
    // 수도 목록 (국가에 따라 수도가 여러 개인 경우가 있어 List로 관리)
    private List<String> capital;
    // 대륙/지역 (예: "Asia")
    private String region;
    // 세부 지역 (예: "Eastern Asia")
    private String subregion;
    // 통화 코드(key, 예: "KRW") -> 통화 상세 정보(value) 매핑
    private Map<String, Currency> currencies;
    // 언어 코드(key) -> 언어 이름(value) 매핑 (예: {"kor": "Korean"})
    private Map<String, String> languages;
    // 국가 대표 좌표 [위도, 경도]
    private List<Double> latlng;
    // 국기 이미지 URL 정보를 담는 중첩 객체
    private Flags flags;

    /**
     * 국가 이름 정보를 담는 중첩 클래스 (일반명 / 공식명).
     */
    @Getter
    @Setter
    public static class Name {
        // 일반적으로 통용되는 국가명 (예: "South Korea")
        private String common;
        // 공식 국가명 (예: "Republic of Korea")
        private String official;
    }

    /**
     * 통화 상세 정보를 담는 중첩 클래스.
     */
    @Getter
    @Setter
    public static class Currency {
        // 통화 이름 (예: "South Korean won")
        private String name;
        // 통화 기호 (예: "₩")
        private String symbol;
    }

    /**
     * 국기 이미지 URL 정보를 담는 중첩 클래스.
     */
    @Getter
    @Setter
    public static class Flags {
        // PNG 형식 국기 이미지 URL
        private String png;
        // SVG 형식 국기 이미지 URL
        private String svg;
    }

}
