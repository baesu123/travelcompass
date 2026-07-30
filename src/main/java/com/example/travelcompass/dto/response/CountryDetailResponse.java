package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 국가 상세 페이지에 필요한 모든 정보를 한데 모아 클라이언트(화면)에게 응답으로
 * 내려주기 위한 종합 DTO.
 * (내부 서비스 -> 컨트롤러 -> 클라이언트 응답용. 여러 외부 API 결과를 서비스 계층에서
 * 조합한 뒤 이 DTO 하나로 묶어서 응답한다.)
 * - 국가 기본 정보 : RestCountries(정적 countries.json) 조회 결과
 * - 주요 관광지 : Wikipedia Geo Search 조회 결과
 * - 환율 : Frankfurter 등 환율 API 조회 결과
 * - 날씨 : Open-Meteo 16일 예보 조회 결과
 * - 평균 기후 : 프로젝트 내부 정적 데이터
 * - 즐겨찾기 여부/닉네임 : 로그인 세션 기반 부가 정보
 *
 * Lombok 사용 팁:
 * - @Getter : 각 필드에 대한 getter를 자동 생성.
 * - @Builder : CountryDetailResponse.builder()...build() 형태로, 필드가 많아도
 *   어떤 값이 어떤 필드에 들어가는지 명확하게 객체를 생성할 수 있게 해준다.
 * - @AllArgsConstructor : 모든 필드를 받는 생성자를 자동 생성 (Builder가 내부적으로 사용).
 */
@Getter
@Builder
@AllArgsConstructor
public class CountryDetailResponse {

    // 국가 기본 정보 (RestCountries)
    // 국가 코드 (예: "KR")
    private String countryCode;
    // 일반적으로 통용되는 국가명 (예: "South Korea")
    private String commonName;
    // 공식 국가명 (예: "Republic of Korea")
    private String officialName;
    // 수도 목록
    private List<String> capital;
    // 대륙/지역
    private String region;
    // 세부 지역
    private String subregion;
    // 통화 코드 -> 통화 상세 정보 매핑 (RestCountryResponse.Currency 재사용)
    private Map<String, RestCountryResponse.Currency> currencies;
    // 언어 코드 -> 언어 이름 매핑
    private Map<String, String> languages;
    // 국가 대표 좌표 [위도, 경도]
    private List<Double> latlng;
    // 국기 이미지 URL 정보 (RestCountryResponse.Flags 재사용)
    private RestCountryResponse.Flags flags;

    // 주요 관광지 (Wiki)
    // Wikipedia Geo Search로 조회한 관광지(장소) 목록
    private List<AttractionInfo> attractions;

    // 환율 (현지통화 1단위 = 원화)
    // 현지 통화 코드 (예: "USD")
    private String currencyCode;
    // 현지 통화 1단위를 원화로 환산한 환율 (null 가능: 환율 조회 실패 시)
    private Double exchangeRateToKrw;

    // 날씨 (16일 예보)
    // 예보 날짜 목록
    private List<String> forecastDates;
    // 날짜별 최고 기온 목록
    private List<Double> temperatureMax;
    // 날짜별 최저 기온 목록
    private List<Double> temperatureMin;
    // 날짜별 강수 확률 목록
    private List<Integer> precipitationProbability;

    // 16일 이후 평균 기후 정보 (정적 데이터)
    // 평균 기온
    private double averageTemperature;
    // 평균 강수량
    private double averagePrecipitation;
    // 추천 복장 안내
    private String recommendedClothing;
    // 여행 팁 안내
    private String travelTip;

    // 로그인/즐겨찾기 정보
    // 현재 로그인한 회원이 이 국가를 즐겨찾기로 등록했는지 여부
    private boolean favorite;
    // 현재 로그인한 회원의 닉네임 (비로그인 시 null)
    private String memberNickname;

    /**
     * 국가 상세 페이지에 노출할 관광지(장소) 정보 하나를 담는 중첩 클래스.
     * Wikipedia Geo Search 결과(WikipediaGeoSearchResponse.GeoSearchItem)를 서비스에서
     * 가공해 채워 넣는다.
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class AttractionInfo {
        // 관광지(장소) 이름
        private String title;
        // 관광지 위도
        private double lat;
        // 관광지 경도
        private double lon;
        // 국가 기준 좌표로부터 관광지까지의 거리 (단위: 미터)
        private double distanceMeters;
    }

}
