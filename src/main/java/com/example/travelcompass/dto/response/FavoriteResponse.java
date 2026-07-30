package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 로그인한 회원이 즐겨찾기(관심 국가)로 등록한 항목 하나를 클라이언트에게 응답으로
 * 내려주기 위한 DTO.
 * (내부 서비스 -> 컨트롤러 -> 클라이언트 응답용. 외부 API 매핑용이 아님)
 *
 * Lombok 사용 팁:
 * - @Getter : 각 필드에 대한 getter를 자동 생성.
 * - @Builder : FavoriteResponse.builder()...build() 형태로 가독성 좋게 객체를 생성.
 * - @AllArgsConstructor : 모든 필드를 받는 생성자를 자동 생성 (Builder가 내부적으로 사용).
 */
@Getter
@Builder
@AllArgsConstructor
public class FavoriteResponse {

    // 즐겨찾기 항목의 고유 식별자 (DB의 PK)
    private Long id;
    // 즐겨찾기로 등록한 국가의 코드 (예: "KR")
    private String countryCode;
    // 즐겨찾기로 등록한 국가의 이름 (화면 표시용)
    private String countryName;
    // 즐겨찾기로 등록한 일시
    private LocalDateTime createdAt;

}
