package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 리뷰(ReviewResponse)에 달린 댓글 하나를 클라이언트에게 응답으로 내려주기 위한 DTO.
 * (내부 서비스 -> 컨트롤러 -> 클라이언트 응답용. 외부 API 매핑용이 아님)
 *
 * Lombok 사용 팁:
 * - @Getter : 각 필드에 대한 getter를 자동 생성.
 * - @Builder : 빌더 패턴으로 객체를 생성할 수 있게 해준다. (예: CommentResponse.builder()...build())
 * - @AllArgsConstructor : 모든 필드를 받는 생성자를 자동 생성 (Builder가 내부적으로 사용).
 */
@Getter
@Builder
@AllArgsConstructor
public class CommentResponse {

    // 댓글의 고유 식별자 (DB의 PK)
    private Long id;
    // 댓글 작성자의 닉네임
    private String authorNickname;
    // 댓글 내용
    private String content;
    // 댓글이 작성된 일시
    private LocalDateTime createdAt;

}
