package com.example.travelcompass.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 여행 후기(Review)에 달리는 댓글 하나를 나타내는 엔티티 클래스.
 * DB의 comment 테이블 한 행과 매핑되며, MyBatis 매퍼가 SQL 결과를 이 객체로 변환한다.
 *
 * Lombok 어노테이션 설명(초보자용 팁):
 * - @Getter/@Setter: 필드별 접근자(getter)/설정자(setter) 메서드를 자동 생성.
 * - @NoArgsConstructor/@AllArgsConstructor: 기본 생성자와 전체 필드 생성자를 자동 생성.
 * - @Builder: Comment.builder()...build() 형태의 빌더 패턴 지원.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    // 댓글 고유 번호 (PK)
    private Long id;
    // 이 댓글이 달린 후기(Review)의 id (FK, 어떤 후기에 대한 댓글인지 연결)
    private Long reviewId;
    // 댓글을 작성한 회원의 id (FK, 작성자 식별용)
    private Long memberId;
    // 댓글 내용(본문 텍스트)
    private String content;
    // 댓글이 최초 작성된 시각
    private LocalDateTime createdAt;
    // 댓글이 마지막으로 수정된 시각
    private LocalDateTime updatedAt;

}
