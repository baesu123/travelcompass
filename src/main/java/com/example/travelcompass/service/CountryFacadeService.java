package com.example.travelcompass.service;

import com.example.travelcompass.client.FrankfurterClient;
import com.example.travelcompass.client.OpenMeteoClient;
import com.example.travelcompass.client.RestCountriesClient;
import com.example.travelcompass.client.WikidataClient;
import com.example.travelcompass.common.exception.BusinessException;
import com.example.travelcompass.common.exception.ErrorCode;
import com.example.travelcompass.dto.response.CountryDetailResponse;
import com.example.travelcompass.dto.response.FrankfurterResponse;
import com.example.travelcompass.dto.response.OpenMeteoResponse;
import com.example.travelcompass.dto.response.RestCountryResponse;
import com.example.travelcompass.dto.response.WikipediaGeoSearchResponse;
import com.example.travelcompass.mapper.CountryMapper;
import com.example.travelcompass.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CountryFacadeService {

    private final RestCountriesClient restCountriesClient;
    private final WikidataClient wikidataClient;
    private final FrankfurterClient frankfurterClient;
    private final OpenMeteoClient openMeteoClient;
    private final FavoriteMapper favoriteMapper;
    private final CountryMapper countryMapper;

    public CountryDetailResponse getCountryDetail(String countryCode, Long memberId, String memberNickname) {
        RestCountryResponse country = restCountriesClient.getCountryByCode(countryCode)
                .block(Duration.ofSeconds(5));

        if (country == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "존재하지 않는 국가 코드입니다: " + countryCode);
        }

        boolean hasCoordinates = country.getLatlng() != null && country.getLatlng().size() >= 2;
        double lat = hasCoordinates ? country.getLatlng().get(0) : 0;
        double lon = hasCoordinates ? country.getLatlng().get(1) : 0;

        String currencyCode = country.getCurrencies() != null
                ? country.getCurrencies().keySet().stream().findFirst().orElse(null)
                : null;

        Mono<WikipediaGeoSearchResponse> wikiMono = hasCoordinates
                ? wikidataClient.getNearbyPlaces(lat, lon).onErrorReturn(new WikipediaGeoSearchResponse())
                : Mono.just(new WikipediaGeoSearchResponse());

        Mono<OpenMeteoResponse> weatherMono = hasCoordinates
                ? openMeteoClient.getForecast(lat, lon).onErrorReturn(new OpenMeteoResponse())
                : Mono.just(new OpenMeteoResponse());

        // Frankfurter API는 동일 통화 쌍(KRW->KRW)을 조회하면 "bad currency pair" 에러를 반환하므로
        // 외부 호출 없이 환율 1.0으로 직접 응답을 구성한다.
        Mono<FrankfurterResponse> exchangeMono;
        if (currencyCode == null) {
            exchangeMono = Mono.just(new FrankfurterResponse());
        } else if (currencyCode.equals("KRW")) {
            FrankfurterResponse sameCurrency = new FrankfurterResponse();
            sameCurrency.setBase("KRW");
            sameCurrency.setAmount(1.0);
            sameCurrency.setRates(Map.of("KRW", 1.0));
            exchangeMono = Mono.just(sameCurrency);
        } else {
            exchangeMono = frankfurterClient.getLatestRate("KRW", currencyCode).onErrorReturn(new FrankfurterResponse());
        }

        // MyBatis 호출은 블로킹이므로, 나머지 리액티브 체인(WebClient)과 같은 이벤트 루프 스레드를
        // 막지 않도록 별도의 boundedElastic 스케줄러에서 실행한다.
        Mono<Boolean> favoriteMono = memberId != null
                ? Mono.fromCallable(() -> favoriteMapper.countByMemberIdAndCountryCode(memberId, country.getCca2()) > 0)
                        .subscribeOn(Schedulers.boundedElastic())
                        .onErrorReturn(false)
                : Mono.just(false);

        // 국가 상세 화면은 REST Countries/Wiki/환율/날씨/즐겨찾기 5개 정보를 하나로 합쳐 보여줘야 하므로,
        // 서로 독립적인 외부 API 호출들을 Mono.zip으로 병렬 실행해 응답 시간을 단축한다.
        // 각 Mono는 onErrorReturn으로 실패를 흡수하므로, 외부 API 하나가 죽어도 나머지 정보는 정상 표시된다.
        return Mono.zip(wikiMono, weatherMono, exchangeMono, favoriteMono)
                .map(tuple -> countryMapper.toCountryDetailResponse(
                        country, tuple.getT1(), tuple.getT3(), tuple.getT2(), tuple.getT4(), memberNickname))
                .block(Duration.ofSeconds(10));
    }

}
