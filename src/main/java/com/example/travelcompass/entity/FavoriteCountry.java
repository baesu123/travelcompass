package com.example.travelcompass.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 회원이 즐겨찾기(관심 국가)로 등록한 국가 정보를 나타내는 엔티티 클래스.
 * DB의 favorite_country 테이블 한 행과 매핑된다.
 *
 * Lombok 어노테이션 설명(초보자용 팁):
 * - @Getter/@Setter: 필드별 getter/setter 자동 생성.
 * - @NoArgsConstructor/@AllArgsConstructor: 기본 생성자/전체 필드 생성자 자동 생성.
 * - @Builder: FavoriteCountry.builder()...build() 형태로 객체 생성 가능.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteCountry {

    // 즐겨찾기 항목 고유 번호 (PK)
    private Long id;
    // 즐겨찾기를 등록한 회원의 id (FK)
    private Long memberId;
    // 국가 코드 (예: "KR", "US" 등 ISO 국가 코드, RestCountries/countries.json에서 사용하는 식별자)
    private String countryCode;
    // 국가 이름 (예: "대한민국", 화면 표시용 국가명)
    private String countryName;
    // 즐겨찾기로 등록된 시각
    private LocalDateTime createdAt;

}
