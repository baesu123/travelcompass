package com.example.travelcompass.client;

import com.example.travelcompass.dto.response.WikipediaGeoSearchResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 위키백과(Wikipedia) GeoSearch API를 호출해 특정 위경도 주변의 명소/장소 정보를 가져오는 클라이언트.
 * 클래스 이름은 WikidataClient지만 실제로는 Wikidata가 아닌 Wikipedia의 geosearch 액션 API를 사용한다.
 */
@Component
public class WikidataClient {

    // 위키백과 API 전용으로 baseUrl이 설정된 WebClient 인스턴스
    private final WebClient webClient;

    /**
     * 공용 WebClient.Builder를 주입받아 위키백과 API 전용 baseUrl을 지정한 WebClient를 생성한다.
     *
     * @param webClientBuilder 스프링 빈으로 등록된 공용 WebClient 빌더
     */
    public WikidataClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://en.wikipedia.org/w/api.php")
                .build();
    }

    /**
     * 지정한 위도/경도 반경 10km 이내에 있는 위키백과 문서(명소 등) 최대 10건을 검색한다.
     *
     * @param latitude  기준 위치의 위도
     * @param longitude 기준 위치의 경도
     * @return 주변 장소 검색 결과를 담은 Mono (비동기 결과, 구독 시점에 실제 요청이 실행됨)
     */
    public Mono<WikipediaGeoSearchResponse> getNearbyPlaces(double latitude, double longitude) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        // action=query, list=geosearch: 위경도 기반 지리 검색을 요청하는 MediaWiki API 표준 파라미터
                        .queryParam("action", "query")
                        .queryParam("list", "geosearch")
                        // gscoord: "위도|경도" 형식의 검색 기준 좌표
                        .queryParam("gscoord", latitude + "|" + longitude)
                        // gsradius: 검색 반경(미터 단위), gslimit: 최대 결과 개수
                        .queryParam("gsradius", 10000)
                        .queryParam("gslimit", 10)
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .bodyToMono(WikipediaGeoSearchResponse.class); // 응답 JSON을 WikipediaGeoSearchResponse로 변환
    }

}
