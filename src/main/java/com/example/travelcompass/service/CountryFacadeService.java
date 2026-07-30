package com.example.travelcompass.service;

import com.example.travelcompass.client.FrankfurterClient;
import com.example.travelcompass.client.OpenMeteoClient;
import com.example.travelcompass.client.RestCountriesClient;
import com.example.travelcompass.client.WikidataClient;
import com.example.travelcompass.common.exception.BusinessException;
import com.example.travelcompass.common.exception.ErrorCode;
import com.example.travelcompass.dto.response.ClimateInfo;
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

/**
 * 국가 상세 화면에 필요한 여러 정보(국가 기본 정보, 주변 명소, 날씨, 환율, 즐겨찾기 여부, 기후)를
 * 한 번에 모아서 응답하는 파사드(Facade) 서비스.
 * 여러 외부 API 클라이언트(WebClient 기반)와 DB Mapper를 조합해야 하므로, 컨트롤러가 이 복잡함을
 * 몰라도 되도록 CountryFacadeService 하나만 호출하면 되게끔 감싸주는 역할을 한다. (파사드 패턴)
 */
@Service
@RequiredArgsConstructor // final 필드 7개(클라이언트 4개 + Mapper 2개 + ClimateService)를 생성자 주입받는다.
public class CountryFacadeService {

    private final RestCountriesClient restCountriesClient; // 국가 기본 정보(이름, 통화, 좌표 등) 조회
    private final WikidataClient wikidataClient; // 좌표 기반 주변 명소(위키) 정보 조회
    private final FrankfurterClient frankfurterClient; // 환율 정보 조회
    private final OpenMeteoClient openMeteoClient; // 날씨(기상) 정보 조회
    private final FavoriteMapper favoriteMapper; // 즐겨찾기 여부 확인용 DB Mapper
    private final CountryMapper countryMapper; // 여러 조각의 응답을 CountryDetailResponse로 합쳐주는 매퍼(변환 전담)
    private final ClimateService climateService; // 지역별 평균 기후 정보 제공 서비스

    /**
     * 국가 코드 하나로 상세 화면에 필요한 모든 정보를 조합해서 반환한다.
     * REST Countries, Wikidata, Frankfurter, OpenMeteo 등 서로 독립적인 외부 API 호출들을
     * 병렬로 실행해 전체 응답 시간을 단축하는 것이 이 메서드의 핵심 설계다.
     * @param countryCode 상세 정보를 조회할 국가 코드 (예: "KR", "JP")
     * @param memberId 로그인한 회원의 ID (비로그인이면 null, 즐겨찾기 여부 확인에만 사용)
     * @param memberNickname 로그인한 회원의 닉네임 (화면 표시용, 응답 DTO에 그대로 실어 보냄)
     * @return 국가 상세 정보(기본 정보 + 명소 + 날씨 + 환율 + 기후 + 즐겨찾기 여부)를 모두 합친 응답 DTO
     */
    public CountryDetailResponse getCountryDetail(String countryCode, Long memberId, String memberNickname) {
        // 국가 기본 정보는 이후 로직(좌표, 통화 등)의 전제 조건이 되므로 먼저 블로킹으로 받아온다.
        // WebClient는 원래 비동기(Mono)지만, 이 메서드 자체가 동기 방식이라 block()으로 결과를 기다린다.
        RestCountryResponse country = restCountriesClient.getCountryByCode(countryCode)
                .block(Duration.ofSeconds(5));

        if (country == null) {
            // 존재하지 않는 국가 코드로 조회하면 이후 로직을 진행할 수 없으므로 즉시 예외를 던진다.
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "존재하지 않는 국가 코드입니다: " + countryCode);
        }

        // 위도/경도 데이터가 없는 국가도 있으므로, 있는 경우에만 좌표 기반 API(위키, 날씨)를 호출한다.
        boolean hasCoordinates = country.getLatlng() != null && country.getLatlng().size() >= 2;
        double lat = hasCoordinates ? country.getLatlng().get(0) : 0;
        double lon = hasCoordinates ? country.getLatlng().get(1) : 0;

        // 국가가 사용하는 통화가 여러 개일 수 있으나, 화면에는 대표 통화 하나만 보여주므로 첫 번째 것만 사용.
        String currencyCode = country.getCurrencies() != null
                ? country.getCurrencies().keySet().stream().findFirst().orElse(null)
                : null;

        // 좌표가 없으면 위키 API를 호출할 수 없으므로, 빈 응답 객체를 즉시 담은 Mono로 대체한다.
        // onErrorReturn으로 외부 API 실패 시에도 예외가 전체 흐름을 멈추지 않고 빈 값으로 대체되게 한다.
        Mono<WikipediaGeoSearchResponse> wikiMono = hasCoordinates
                ? wikidataClient.getNearbyPlaces(lat, lon).onErrorReturn(new WikipediaGeoSearchResponse())
                : Mono.just(new WikipediaGeoSearchResponse());

        Mono<OpenMeteoResponse> weatherMono = hasCoordinates
                ? openMeteoClient.getForecast(lat, lon).onErrorReturn(new OpenMeteoResponse())
                : Mono.just(new OpenMeteoResponse());

        // Frankfurter API는 동일 통화 쌍(KRW->KRW)을 조회하면 "bad currency pair" 에러를 반환하므로
        // 외부 호출 없이 환율 1.0으로 직접 응답을 구성한다.
        // 화면에는 "현지통화 100 = 원화" 형태로 보여주므로, 현지통화 -> KRW 방향으로 조회한다.
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
            exchangeMono = frankfurterClient.getLatestRate(currencyCode, "KRW").onErrorReturn(new FrankfurterResponse());
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
        ClimateInfo climateInfo = climateService.getClimateInfo(country.getRegion());

        return Mono.zip(wikiMono, weatherMono, exchangeMono, favoriteMono)
                .map(tuple -> countryMapper.toCountryDetailResponse(
                        country, tuple.getT1(), tuple.getT3(), tuple.getT2(), climateInfo, tuple.getT4(), memberNickname))
                .block(Duration.ofSeconds(10));
    }

}
