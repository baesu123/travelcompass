package com.example.travelcompass.mapper;

import com.example.travelcompass.dto.response.ClimateInfo;
import com.example.travelcompass.dto.response.CountryDetailResponse;
import com.example.travelcompass.dto.response.FrankfurterResponse;
import com.example.travelcompass.dto.response.OpenMeteoResponse;
import com.example.travelcompass.dto.response.RestCountryResponse;
import com.example.travelcompass.dto.response.WikipediaGeoSearchResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 국가 상세 정보 응답(CountryDetailResponse)을 조립하는 변환 전용 컴포넌트.
 *
 * <p>주의: 이름은 "Mapper"이지만 다른 Mapper 인터페이스들과 달리 MyBatis {@code @Mapper}가 아니라
 * 스프링 {@code @Component}로 등록되는 일반 자바 클래스다. 즉, 이 클래스는 DB를 직접 조회하지 않고,
 * 외부 API(RestCountries, Wikipedia, Frankfurter 환율, OpenMeteo 날씨 등)에서 이미 받아온
 * 응답 객체들을 조합해서 화면(또는 API 응답)에 필요한 {@link CountryDetailResponse} 하나로
 * 합쳐주는 "데이터 매핑/변환" 역할만 수행한다.</p>
 */
@Component
public class CountryMapper {

    /**
     * 여러 외부 API 응답과 즐겨찾기/닉네임 정보를 조합하여 국가 상세 응답 DTO를 생성한다.
     *
     * @param country        RestCountries API에서 받은 국가 기본 정보(국가명, 수도, 지역, 통화, 언어, 국기 등)
     * @param wiki           Wikipedia 지오서치 API에서 받은 주변 관광지(위키 문서) 검색 결과
     * @param exchange       Frankfurter 환율 API에서 받은 기준 통화 및 환율 정보
     * @param weather        OpenMeteo 날씨 API에서 받은 일자별 예보(최고/최저 기온, 강수확률 등)
     * @param climateInfo    평균 기후(평균 기온/강수량) 및 추천 복장, 여행 팁을 담은 정보
     * @param favorite       현재 로그인한 회원이 이 국가를 즐겨찾기했는지 여부
     * @param memberNickname 즐겨찾기 등록 시 함께 표시할 회원 닉네임 (없으면 null 가능)
     * @return 화면/응답에 필요한 모든 국가 상세 정보를 담은 {@link CountryDetailResponse} 객체
     */
    public CountryDetailResponse toCountryDetailResponse(RestCountryResponse country,
                                                           WikipediaGeoSearchResponse wiki,
                                                           FrankfurterResponse exchange,
                                                           OpenMeteoResponse weather,
                                                           ClimateInfo climateInfo,
                                                           boolean favorite,
                                                           String memberNickname) {

        OpenMeteoResponse.Daily daily = weather.getDaily();

        return CountryDetailResponse.builder()
                .countryCode(country.getCca2())
                .commonName(country.getName().getCommon())
                .officialName(country.getName().getOfficial())
                .capital(country.getCapital())
                .region(country.getRegion())
                .subregion(country.getSubregion())
                .currencies(country.getCurrencies())
                .languages(country.getLanguages())
                .latlng(country.getLatlng())
                .flags(country.getFlags())
                .attractions(toAttractions(wiki))
                .currencyCode(exchange.getBase())
                .exchangeRateToKrw(findExchangeRate(exchange))
                .forecastDates(daily != null ? daily.getTime() : Collections.emptyList())
                .temperatureMax(daily != null ? daily.getTemperature2mMax() : Collections.emptyList())
                .temperatureMin(daily != null ? daily.getTemperature2mMin() : Collections.emptyList())
                .precipitationProbability(daily != null ? daily.getPrecipitationProbabilityMax() : Collections.emptyList())
                .averageTemperature(climateInfo.getAverageTemperature())
                .averagePrecipitation(climateInfo.getAveragePrecipitation())
                .recommendedClothing(climateInfo.getRecommendedClothing())
                .travelTip(climateInfo.getTravelTip())
                .favorite(favorite)
                .memberNickname(memberNickname)
                .build();
    }

    /**
     * Wikipedia 지오서치 응답에서 주변 관광지(장소) 목록만 추출하여 변환한다.
     * 응답 구조상 query 또는 geosearch 필드가 없을 수 있으므로(예: API 오류, 검색 결과 없음)
     * null 체크 후 없으면 빈 리스트를 반환해 NPE(NullPointerException)를 방지한다.
     *
     * @param wiki Wikipedia 지오서치 API 원본 응답
     * @return 관광지 제목, 위도/경도, 거리 정보를 담은 리스트 (결과 없으면 빈 리스트)
     */
    private List<CountryDetailResponse.AttractionInfo> toAttractions(WikipediaGeoSearchResponse wiki) {
        if (wiki.getQuery() == null || wiki.getQuery().getGeosearch() == null) {
            return Collections.emptyList();
        }

        return wiki.getQuery().getGeosearch().stream()
                .map(item -> CountryDetailResponse.AttractionInfo.builder()
                        .title(item.getTitle())
                        .lat(item.getLat())
                        .lon(item.getLon())
                        .distanceMeters(item.getDist())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 환율 API 응답에서 첫 번째 환율 값을 찾아 반환한다.
     * rates 맵이 null이거나 비어 있으면(요청한 통화 코드가 잘못되었거나 API 오류인 경우) null을 반환한다.
     * 보통 대상 통화 하나만 요청하므로 첫 번째 값을 그대로 사용해도 안전하다.
     *
     * @param exchange Frankfurter 환율 API 원본 응답
     * @return 기준 통화 대비 환율 값 (없으면 null)
     */
    private Double findExchangeRate(FrankfurterResponse exchange) {
        if (exchange.getRates() == null || exchange.getRates().isEmpty()) {
            return null;
        }
        return exchange.getRates().values().stream().findFirst().orElse(null);
    }

}
