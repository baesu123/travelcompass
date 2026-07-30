package com.example.travelcompass.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 체크리스트 항목의 "완료 여부"만 수정할 때 사용하는 요청 DTO이다.
 *
 * <p>체크박스를 클릭해 완료/미완료 상태를 토글하는 것처럼 단순한 상태 변경 API에서 사용되며,
 * 별도의 필수 입력값 검증(@NotBlank 등)이 필요 없는 boolean 값 하나만 가지고 있어
 * 검증 애노테이션이 붙어 있지 않다.</p>
 */
@Getter
@Setter
public class ChecklistUpdateRequest {

    // 체크리스트 항목의 완료 여부 (true: 완료, false: 미완료). boolean 기본형이라 값 누락 시 기본값 false로 처리됨
    private boolean checked;

}
