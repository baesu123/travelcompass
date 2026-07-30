package com.example.travelcompass.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 특정 국가에 대한 회원의 여행 후기(리뷰)를 나타내는 엔티티 클래스.
 * DB의 review 테이블 한 행과 매핑되며, 이 후기에 대한 댓글은 Comment 엔티티로 별도 관리된다.
 *
 * Lombok 어노테이션 설명(초보자용 팁):
 * - @Getter/@Setter: 필드별 getter/setter 자동 생성.
 * - @NoArgsConstructor/@AllArgsConstructor: 기본 생성자/전체 필드 생성자 자동 생성.
 * - @Builder: Review.builder()...build() 형태로 객체 생성 가능.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    // 후기 고유 번호 (PK)
    private Long id;
    // 후기를 작성한 회원의 id (FK)
    private Long memberId;
    // 후기 대상 국가의 코드 (예: "KR", "JP")
    private String countryCode;
    // 후기 대상 국가의 이름 (화면 표시용)
    private String countryName;
    // 평점 (별점 등 정수형 점수, 예: 1~5)
    private int rating;
    // 후기 본문 내용
    private String content;
    // 후기 작성 시각
    private LocalDateTime createdAt;
    // 후기 마지막 수정 시각
    private LocalDateTime updatedAt;

}
