package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 특정 국가에 대한 여행 후기(리뷰) 하나와 그에 달린 댓글 목록을 클라이언트에게 응답으로
 * 내려주기 위한 DTO.
 * (내부 서비스 -> 컨트롤러 -> 클라이언트 응답용. 외부 API 매핑용이 아님)
 *
 * Lombok 사용 팁:
 * - @Getter : 각 필드에 대한 getter를 자동 생성.
 * - @Builder : ReviewResponse.builder()...build() 형태로 가독성 좋게 객체를 생성.
 * - @AllArgsConstructor : 모든 필드를 받는 생성자를 자동 생성 (Builder가 내부적으로 사용).
 */
@Getter
@Builder
@AllArgsConstructor
public class ReviewResponse {

    // 리뷰의 고유 식별자 (DB의 PK)
    private Long id;
    // 리뷰 작성자의 닉네임
    private String authorNickname;
    // 리뷰 대상 국가 코드 (예: "KR")
    private String countryCode;
    // 리뷰 대상 국가 이름 (화면 표시용)
    private String countryName;
    // 평점 (예: 1~5점)
    private int rating;
    // 리뷰 본문 내용
    private String content;
    // 리뷰가 작성된 일시
    private LocalDateTime createdAt;
    // 이 리뷰에 달린 댓글 목록 (CommentResponse 재사용)
    private List<CommentResponse> comments;

}
