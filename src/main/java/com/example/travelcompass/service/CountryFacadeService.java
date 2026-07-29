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

        Mono<Boolean> favoriteMono = memberId != null
                ? Mono.fromCallable(() -> favoriteMapper.countByMemberIdAndCountryCode(memberId, countryCode) > 0)
                        .subscribeOn(Schedulers.boundedElastic())
                        .onErrorReturn(false)
                : Mono.just(false);

        return Mono.zip(wikiMono, weatherMono, exchangeMono, favoriteMono)
                .map(tuple -> countryMapper.toCountryDetailResponse(
                        country, tuple.getT1(), tuple.getT3(), tuple.getT2(), tuple.getT4(), memberNickname))
                .block(Duration.ofSeconds(10));
    }

}
