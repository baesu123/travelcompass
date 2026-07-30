package com.example.travelcompass.client;

import com.example.travelcompass.dto.response.CountrySearchResultResponse;
import com.example.travelcompass.dto.response.RestCountryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 국가 정보를 제공하는 클라이언트.
 * 이름은 RestCountriesClient이지만, RestCountries 외부 API가 키 인증을 요구하도록 바뀐 이후로는
 * 실제 외부 API를 호출하지 않고, 클래스패스에 내장된 정적 JSON 파일(data/countries.json, data/country-names-ko.json)을
 * 애플리케이션 시작 시 한 번 읽어서 메모리(Map)에 올려두고 그 데이터를 그대로 서빙한다.
 * 초보자 팁: 외부 네트워크 호출이 없으므로 이 클래스의 조회 메서드들은 WebClient/Mono를 쓰더라도
 * 이미 메모리에 있는 값을 감싸서 반환할 뿐 실제 비동기 IO가 일어나지 않는다.
 */
@Component
public class RestCountriesClient {

    // 국가 목록 JSON 파일의 클래스패스 경로 (resources/data/countries.json)
    private static final String DATA_PATH = "data/countries.json";
    // 국가 코드-한글명 매핑 JSON 파일의 클래스패스 경로
    private static final String KOREAN_NAMES_PATH = "data/country-names-ko.json";

    // 국가 코드(cca2, 예: "KR") -> 국가 상세 정보를 담은 조회용 캐시(Map). 생성자에서 한 번만 로딩된다.
    private final Map<String, RestCountryResponse> countriesByCode;
    // 국가 코드 -> 한글 국가명을 담은 조회용 캐시(Map)
    private final Map<String, String> koreanNamesByCode;

    /**
     * 빈 생성 시점에 정적 JSON 데이터 파일들을 읽어 메모리에 캐싱해둔다.
     * 초보자 팁: 스프링이 이 빈을 딱 한 번 생성하므로, 파일 읽기(IO)도 애플리케이션 구동 시 한 번만 일어난다.
     *
     * @param objectMapper JSON <-> 자바 객체 변환을 담당하는 Jackson ObjectMapper (스프링이 자동 주입)
     */
    public RestCountriesClient(ObjectMapper objectMapper) {
        this.countriesByCode = loadCountries(objectMapper);
        this.koreanNamesByCode = loadKoreanNames(objectMapper);
    }

    /**
     * 국가 코드로 국가 상세 정보를 조회한다.
     *
     * @param code ISO 국가 코드(예: "kr", "KR" 대소문자 무관)
     * @return 해당 국가 정보를 담은 Mono. 없으면 빈(empty) Mono
     */
    public Mono<RestCountryResponse> getCountryByCode(String code) {
        // Mono.justOrEmpty: Map에서 값을 못 찾아 null이면 자동으로 빈 Mono가 되고, 있으면 그 값을 감싼 Mono가 된다
        return Mono.justOrEmpty(countriesByCode.get(code.toUpperCase()));
    }

    /**
     * 국가 코드에 대응하는 한글 국가명을 조회한다.
     *
     * @param code ISO 국가 코드
     * @return 한글 국가명. 매핑이 없으면 대문자로 변환한 국가 코드를 그대로 반환
     */
    public String getKoreanName(String code) {
        return koreanNamesByCode.getOrDefault(code.toUpperCase(), code.toUpperCase());
    }

    /**
     * 키워드로 국가를 검색한다. 국가 코드 완전 일치, 영문 공식/일반 국가명 부분 일치,
     * 한글 국가명 부분 일치를 모두 지원해 사용자가 "한국", "korea", "kr" 어느 방식으로 검색해도 찾을 수 있게 한다.
     *
     * @param keyword 검색어 (앞뒤 공백은 제거하고 대소문자 구분 없이 비교)
     * @return 검색 조건에 맞는 국가 목록을 한글명 가나다순으로 정렬해서 반환 (없으면 빈 리스트)
     */
    // 국가 코드(2글자)뿐 아니라 영문명/한글명으로도 검색할 수 있도록 부분 일치 검색을 지원한다.
    public List<CountrySearchResultResponse> searchByKeyword(String keyword) {
        String trimmed = keyword.trim();
        if (trimmed.isEmpty()) {
            // 빈 검색어는 검색 결과 없음으로 처리
            return List.of();
        }
        String lower = trimmed.toLowerCase();

        return countriesByCode.values().stream()
                .filter(country -> matches(country, lower)) // 코드/영문명/한글명 중 하나라도 일치하면 통과
                .sorted(Comparator.comparing(country -> displayNameKo(country))) // 한글 표시명 기준 오름차순 정렬
                .map(country -> new CountrySearchResultResponse(
                        country.getCca2(), displayNameKo(country), country.getName().getCommon()))
                .collect(Collectors.toList());
    }

    /**
     * 국가 하나가 주어진 소문자 검색어와 일치하는지 판단한다.
     *
     * @param country      비교 대상 국가 정보
     * @param lowerKeyword 소문자로 변환된 검색어
     * @return 국가 코드 완전 일치, 영문 공식/일반명 부분 일치, 한글명 부분 일치 중 하나라도 해당하면 true
     */
    private boolean matches(RestCountryResponse country, String lowerKeyword) {
        String nameKo = koreanNamesByCode.get(country.getCca2());
        return country.getCca2().equalsIgnoreCase(lowerKeyword)
                || country.getName().getCommon().toLowerCase().contains(lowerKeyword)
                || country.getName().getOfficial().toLowerCase().contains(lowerKeyword)
                || (nameKo != null && nameKo.contains(lowerKeyword));
    }

    /**
     * 화면에 표시할 국가명을 결정한다. 한글명이 있으면 한글명을, 없으면 영문 일반명을 사용한다.
     *
     * @param country 대상 국가 정보
     * @return 화면 표시용 국가명(한글 우선)
     */
    private String displayNameKo(RestCountryResponse country) {
        return koreanNamesByCode.getOrDefault(country.getCca2(), country.getName().getCommon());
    }

    /**
     * 클래스패스의 countries.json 파일을 읽어 국가 코드 -> 국가 정보 Map을 만든다.
     * 각 국가에 대해 flagcdn.com 기반 국기 이미지 URL도 함께 채워 넣는다.
     *
     * @param objectMapper JSON 역직렬화에 사용할 ObjectMapper
     * @return 국가 코드(cca2)를 key로 하는 국가 정보 Map
     */
    private Map<String, RestCountryResponse> loadCountries(ObjectMapper objectMapper) {
        // try-with-resources: InputStream을 다 쓰고 나면 자동으로 close()되어 파일 핸들 누수를 막는다
        try (InputStream inputStream = new ClassPathResource(DATA_PATH).getInputStream()) {
            List<RestCountryResponse> countries = objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, RestCountryResponse.class));

            // JSON 파일 자체에는 국기 이미지 URL이 없으므로, 국가 코드 기반으로 직접 조립해 채워 넣는다
            countries.forEach(country -> country.setFlags(buildFlags(country.getCca2())));

            // 이후 코드로 빠르게 조회할 수 있도록 List를 Map(국가코드 -> 정보)으로 변환
            return countries.stream()
                    .collect(Collectors.toMap(RestCountryResponse::getCca2, country -> country));
        } catch (IOException e) {
            // 파일을 못 읽으면 애플리케이션이 정상 동작할 수 없으므로 즉시 예외를 던져 기동을 실패시킨다
            throw new IllegalStateException("국가 정보 데이터를 불러올 수 없습니다.", e);
        }
    }

    /**
     * 클래스패스의 country-names-ko.json 파일을 읽어 국가 코드 -> 한글 국가명 Map을 만든다.
     *
     * @param objectMapper JSON 역직렬화에 사용할 ObjectMapper
     * @return 국가 코드를 key, 한글 국가명을 value로 하는 Map
     */
    private Map<String, String> loadKoreanNames(ObjectMapper objectMapper) {
        try (InputStream inputStream = new ClassPathResource(KOREAN_NAMES_PATH).getInputStream()) {
            return objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
        } catch (IOException e) {
            throw new IllegalStateException("국가 한글명 데이터를 불러올 수 없습니다.", e);
        }
    }

    /**
     * 국가 코드로부터 flagcdn.com 국기 이미지 URL(png, svg)을 조립한다.
     *
     * @param cca2 ISO 2자리 국가 코드
     * @return png/svg 국기 이미지 URL을 담은 Flags 객체
     */
    private RestCountryResponse.Flags buildFlags(String cca2) {
        String lower = cca2.toLowerCase();
        RestCountryResponse.Flags flags = new RestCountryResponse.Flags();
        flags.setPng("https://flagcdn.com/w320/" + lower + ".png");
        flags.setSvg("https://flagcdn.com/" + lower + ".svg");
        return flags;
    }

}
