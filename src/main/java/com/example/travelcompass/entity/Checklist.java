package com.example.travelcompass.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 여행 준비물(체크리스트) 항목 하나를 나타내는 엔티티 클래스.
 * DB의 checklist 테이블 한 행(row)과 1:1로 매핑되며,
 * MyBatis가 SQL 조회 결과를 이 객체로 변환(매핑)해 준다.
 *
 * Lombok 어노테이션 설명(초보자용 팁):
 * - @Getter/@Setter: 모든 필드에 대해 getXxx()/setXxx() 메서드를 자동 생성한다.
 * - @NoArgsConstructor: 파라미터 없는 기본 생성자를 자동 생성한다. (MyBatis 등이 리플렉션으로 객체를 만들 때 필요)
 * - @AllArgsConstructor: 모든 필드를 파라미터로 받는 생성자를 자동 생성한다.
 * - @Builder: Checklist.builder().id(1L).itemName("여권").build() 처럼 빌더 패턴으로 객체를 생성할 수 있게 해준다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checklist {

    // 체크리스트 항목의 고유 번호 (PK, auto_increment)
    private Long id;
    // 이 항목을 등록한 회원의 id (Member 테이블 FK, 어떤 회원의 체크리스트인지 구분)
    private Long memberId;
    // 준비물 이름 (예: "여권", "충전기" 등 사용자가 입력한 항목명)
    private String itemName;
    // 체크 여부 (true = 준비 완료 체크됨, false = 아직 미완료)
    private boolean checked;
    // 이 항목이 처음 생성된 시각 (DB insert 시각)
    private LocalDateTime createdAt;
    // 이 항목이 마지막으로 수정된 시각 (체크 여부 변경 등으로 update 될 때 갱신)
    private LocalDateTime updatedAt;

}
