package com.example.travelcompass.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 위키백과(Wikipedia) Geo Search API 응답 JSON을 매핑하기 위한 DTO.
 * 특정 위경도 좌표 주변의 위키 문서(주로 관광지/장소 정보)를 검색한 결과를 담는다.
 * Jackson이 JSON 응답을 이 클래스 구조에 맞춰 자동으로 역직렬화(deserialize)한다.
 *
 * Lombok 사용 팁:
 * - @Getter / @Setter : Jackson 역직렬화(setter) 및 이후 서비스 로직에서의 값 조회(getter)를
 *   모두 지원하기 위해 함께 사용한다.
 */
@Getter
@Setter
public class WikipediaGeoSearchResponse {

    // 위키 API 응답의 최상위 "query" 객체
    private Query query;

    /**
     * 위키 API 응답 중 "query" 항목을 매핑하는 중첩 클래스.
     */
    @Getter
    @Setter
    public static class Query {
        // 위경도 주변에서 검색된 장소(문서) 목록
        private List<GeoSearchItem> geosearch;
    }

    /**
     * 위경도 주변 검색 결과 하나(장소/문서 1건)를 나타내는 중첩 클래스.
     */
    @Getter
    @Setter
    public static class GeoSearchItem {
        // 위키 문서의 고유 페이지 ID
        private long pageid;
        // 위키 문서 제목 (관광지/장소 이름)
        private String title;
        // 해당 장소의 위도
        private double lat;
        // 해당 장소의 경도
        private double lon;
        // 검색 기준 좌표로부터의 거리 (단위: 미터)
        private double dist;
    }

}
