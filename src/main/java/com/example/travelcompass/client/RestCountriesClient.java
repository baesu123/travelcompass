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

@Component
public class RestCountriesClient {

    private static final String DATA_PATH = "data/countries.json";
    private static final String KOREAN_NAMES_PATH = "data/country-names-ko.json";

    private final Map<String, RestCountryResponse> countriesByCode;
    private final Map<String, String> koreanNamesByCode;

    public RestCountriesClient(ObjectMapper objectMapper) {
        this.countriesByCode = loadCountries(objectMapper);
        this.koreanNamesByCode = loadKoreanNames(objectMapper);
    }

    public Mono<RestCountryResponse> getCountryByCode(String code) {
        return Mono.justOrEmpty(countriesByCode.get(code.toUpperCase()));
    }

    public String getKoreanName(String code) {
        return koreanNamesByCode.getOrDefault(code.toUpperCase(), code.toUpperCase());
    }

    // 국가 코드(2글자)뿐 아니라 영문명/한글명으로도 검색할 수 있도록 부분 일치 검색을 지원한다.
    public List<CountrySearchResultResponse> searchByKeyword(String keyword) {
        String trimmed = keyword.trim();
        if (trimmed.isEmpty()) {
            return List.of();
        }
        String lower = trimmed.toLowerCase();

        return countriesByCode.values().stream()
                .filter(country -> matches(country, lower))
                .sorted(Comparator.comparing(country -> displayNameKo(country)))
                .map(country -> new CountrySearchResultResponse(
                        country.getCca2(), displayNameKo(country), country.getName().getCommon()))
                .collect(Collectors.toList());
    }

    private boolean matches(RestCountryResponse country, String lowerKeyword) {
        String nameKo = koreanNamesByCode.get(country.getCca2());
        return country.getCca2().equalsIgnoreCase(lowerKeyword)
                || country.getName().getCommon().toLowerCase().contains(lowerKeyword)
                || country.getName().getOfficial().toLowerCase().contains(lowerKeyword)
                || (nameKo != null && nameKo.contains(lowerKeyword));
    }

    private String displayNameKo(RestCountryResponse country) {
        return koreanNamesByCode.getOrDefault(country.getCca2(), country.getName().getCommon());
    }

    private Map<String, RestCountryResponse> loadCountries(ObjectMapper objectMapper) {
        try (InputStream inputStream = new ClassPathResource(DATA_PATH).getInputStream()) {
            List<RestCountryResponse> countries = objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, RestCountryResponse.class));

            countries.forEach(country -> country.setFlags(buildFlags(country.getCca2())));

            return countries.stream()
                    .collect(Collectors.toMap(RestCountryResponse::getCca2, country -> country));
        } catch (IOException e) {
            throw new IllegalStateException("국가 정보 데이터를 불러올 수 없습니다.", e);
        }
    }

    private Map<String, String> loadKoreanNames(ObjectMapper objectMapper) {
        try (InputStream inputStream = new ClassPathResource(KOREAN_NAMES_PATH).getInputStream()) {
            return objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
        } catch (IOException e) {
            throw new IllegalStateException("국가 한글명 데이터를 불러올 수 없습니다.", e);
        }
    }

    private RestCountryResponse.Flags buildFlags(String cca2) {
        String lower = cca2.toLowerCase();
        RestCountryResponse.Flags flags = new RestCountryResponse.Flags();
        flags.setPng("https://flagcdn.com/w320/" + lower + ".png");
        flags.setSvg("https://flagcdn.com/" + lower + ".svg");
        return flags;
    }

}
