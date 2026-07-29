package com.example.travelcompass.client;

import com.example.travelcompass.dto.response.RestCountryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RestCountriesClient {

    private static final String DATA_PATH = "data/countries.json";

    private final Map<String, RestCountryResponse> countriesByCode;

    public RestCountriesClient(ObjectMapper objectMapper) {
        this.countriesByCode = loadCountries(objectMapper);
    }

    public Mono<RestCountryResponse> getCountryByCode(String code) {
        return Mono.justOrEmpty(countriesByCode.get(code.toUpperCase()));
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

    private RestCountryResponse.Flags buildFlags(String cca2) {
        String lower = cca2.toLowerCase();
        RestCountryResponse.Flags flags = new RestCountryResponse.Flags();
        flags.setPng("https://flagcdn.com/w320/" + lower + ".png");
        flags.setSvg("https://flagcdn.com/" + lower + ".svg");
        return flags;
    }

}
