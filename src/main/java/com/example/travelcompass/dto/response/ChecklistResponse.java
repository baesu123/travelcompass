package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 여행 준비물 체크리스트 항목 하나를 클라이언트에게 응답으로 내려주기 위한 DTO.
 * (내부 서비스 -> 컨트롤러 -> 클라이언트 응답용. 외부 API 매핑용이 아님)
 *
 * Lombok 사용 팁:
 * - @Getter : id, itemName, checked 각각에 대한 getter 메서드를 자동으로 생성해준다.
 * - @Builder : ChecklistResponse.builder().id(1L).itemName("여권").checked(true).build() 형태로
 *              객체를 가독성 좋게 생성할 수 있게 해준다.
 * - @AllArgsConstructor : 모든 필드를 파라미터로 받는 생성자를 자동 생성한다. @Builder가 내부적으로
 *              이 생성자를 사용한다.
 */
@Getter
@Builder
@AllArgsConstructor
public class ChecklistResponse {

    // 체크리스트 항목의 고유 식별자 (DB의 PK)
    private Long id;
    // 체크리스트 항목 이름 (예: "여권", "충전기" 등)
    private String itemName;
    // 해당 항목을 챙겼는지(체크했는지) 여부
    private boolean checked;

}
